package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class HttpRequestUtils {
    /**
     * @param queryString 은 URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
     * @return
     */
    public static Map<String, String> parseQueryString(String queryString) {
        return parseValues(queryString, "&");
    }

    /**
     * @param cookies 쿠키값은 name1=value1; name2=value2 형식임
     * @return
     */
    public static Map<String, String> parseCookies(String cookies) {
        return parseValues(cookies, ";");
    }

    private static Map<String, String> parseValues(String values, String separator) {
        if (Strings.isNullOrEmpty(values)) {
            return Maps.newHashMap();
        }

        String[] tokens = values.split(separator);
        return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(p -> p != null)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    static Pair getKeyValue(String keyValue, String regex) {
        if (Strings.isNullOrEmpty(keyValue)) {
            return null;
        }

        String[] tokens = keyValue.split(regex);
        if (tokens.length != 2) {
            return null;
        }

        return new Pair(tokens[0], tokens[1]);
    }

    /**
     * queryString을 포함한 url 문자열을 리턴한다.
     * @param requestLine
     * @return
     */
    public String getFullUrl(final String requestLine) {
        return this.getUrl(requestLine, "\\s");
    }

    /**
     * queryString을 제외한 주소 url 문자열을 리턴한다.
     * @param requestLine
     * @return
     */
    public String getUrl(final String requestLine) {
        return this.getUrl(requestLine, "\\s|\\?");
    }

    private String getUrl(final String requestLine, final String delimiter) {
        if(requestLine == null) {
            return "";
        }

        final String[] strs = requestLine.split(delimiter);

        if(strs.length < 2) {
            return "";
        }

        return strs[1];
    }

    public String getQueryString(final String url) {
        if(url == null || url.isEmpty()) {
            return "";
        }

        final String regex = "^[^\\?]*\\?(.*)$";
        final Matcher m = Pattern.compile(regex).matcher(url);

        if(!m.find()) {
            return "";
        }
        return m.group(1);
    }

    public static Pair parseHeader(String header) {
        return getKeyValue(header, ": ");
    }

    public List<String> getRequestLineList(final BufferedReader br, final String line) throws IOException {
        final List<String> requestLineList = new LinkedList<>();

        String tempLine = line;
        while( ! tempLine.equals("")) {
            tempLine = br.readLine();
            requestLineList.add(tempLine);
        }
        return requestLineList;
    }

    /**
     * request 문자열에서 Content-Length 를 명시한 라인을 찾아 문자열 길이를 리턴한다.
     * 만약 찾지 못한다면 -1 을 리턴한다.
     * @param requestLineList
     * @return
     */
    public int getRequestContentsLength(final List<String> requestLineList) {
        final String lengthStr = requestLineList.stream()
                .filter(l -> l.matches("Content-Length\\s*:\\s*(?:\\d+)"))
                .findFirst()
                .orElse(":-1")
                ;
        final int length = Integer.parseInt(lengthStr.split("\\s*:\\s*")[1]);
        return length;
    }

    /**
     * request body 를 리턴한다.
     * @param requestUtils
     * @param br
     * @param requestLineList
     * @return
     * @throws IOException
     */
    public String getBody(HttpRequestUtils requestUtils, BufferedReader br, List<String> requestLineList) throws IOException {
        final int contentLength = requestUtils.getRequestContentsLength(requestLineList);
        if(contentLength < 0) {
            return "";
        }
        return IOUtils.readData(br, contentLength);
    }

    public static class Pair {
        String key;
        String value;

        Pair(String key, String value) {
            this.key = key.trim();
            this.value = value.trim();
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Pair other = (Pair) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Pair [key=" + key + ", value=" + value + "]";
        }
    }
}
