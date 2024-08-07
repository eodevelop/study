## 레디스 정리

### 레디스 등장배경
- 기존 데이터 베이스의 성능 및 확장성 문제 해결
    - 실시간 웹 로그 분석 도구 개발 중 MySQL 로는 필요한 성능을 충족시킬 수 없다는 문제 해결하기 위해 메모리 기반 고속 데이터베이스를 만들게 되었고 이것이 레디스 개발의 출발점.
- 데이터 구조 지원
    - 기존의 키값 저장소들은 단순한 문자열 데이터를 저장하는데 그쳐서 이를 보완하기 위한 리스트, 셋, 해시, 정렬된 셋등 다양한 데이터 구조를 지원해서 이 요구를 충족 시켰음.   

### 레디스 주요 특징
#### 인메모리 데이터 저장소
- 데이터를 메모리에 저장하여 매우 빠른 읽기 및 쓰기 성능을 제공
#### 풍부한 데이터 구조 지원
- 문자열, 리스트, 셋, 해시, 정렬된 셋, 비트맵, 공간 인덱스 등 다양한 데이터 구조를 지원
#### 복제 및 고가용성
- 마스터-슬레이브 구조를 통해 데이터 복제 및 고가용성을 제공
#### 영속성 옵션
- 스냅샷 및 AOF(Append Only File) 방식을 통해 데이터를 디스크에 영구적으로 저장 가능
#### 스크립팅
- Lua 스크립트를 지원하여 서버 측에서는 복잡한 작업을 효율적으로 수행 가능    
