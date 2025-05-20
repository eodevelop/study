import java.util.Map;

/**
 * Map으로 변환 가능한 객체를 정의하는 인터페이스
 */
public interface MapSerializable {

    Map<String, Object> convert();
}