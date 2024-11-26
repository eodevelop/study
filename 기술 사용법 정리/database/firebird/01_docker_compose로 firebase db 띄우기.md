## 순서
### 1. Firebird 공식 docker 이미지 다운
- 설치를 진행할 docker 가 설치된 서버로 이동합니다.
- 다음 명령어로 pull을 진행합니다.
```bash
docker pull jacobalberty/firebird
```

### 2. docker-compose를 실행하기 위한 docker-compose.yml 파일 생성
- 다음과 같이 docker-compose.yml 파일을 작성합니다.
```yml
version: "3.3"
services:
  firebird:
    image: jacobalberty/firebird
    container_name: firebird
    environment:
      ISC_PASSWORD: "test1234"
      FIREBIRD_USER: "test"
      FIREBIRD_PASSWORD: "test1234"
      FIREBIRD_DATABASE: "test.fdb"
    ports:
      - "3050:3050"
    volumes:
      - /tmp/firebird_data:/firebird/data
volumes:
  firebird_data:
```


### 3. 로컬에 DB의 저장소가될 공간 생성
- 다음 명령어로 생성합니다.
```bash
sudo mkdir -p /tmp/firebird_data
sudo chmod -R 777 /tmp/firebird_data
```

### 4. docker-compose 실행 후 정상 동작 확인
- 위 파일이 생성된 위치에서 다음 명령어로 실행합니다.
```bash
docker-compose up -d
```
- docker ps 명령어로 정상적으로 실행되었는지 확인합니다.
- 정상적으로 실행되었다면 다음명령어로 `test.fdb` 파일이 생성되었는지 확인합니다.
```bash
ls /tmp/firebird_data
```
- 다음 명령어로 firebird에 접속하여 정상적으로 `test.fdb` 파일이 생성되었는지 확인합니다.
```bash
docker exec -it firebird ls /firebird/data
```

### 5. DB 접속 테스트
- dbeaver로 접속하여 정상적으로 접속되는지 확인합니다.
- 접속정보는 다음과 같습니다.
  - `Host`: 사용한 서버의 IP
  - `Port`: 3050
  - `Database`: test.fdb
  - `User`: test
  - `Password`: test1234
- 또는 해당 url 정보로 접속하여 정상적으로 접속되는지 확인합니다.
```url
jdbc:firebirdsql://사용한 서버의 IP:3050/test.fdb
```

## 추가 - Spring Boot 에서 Firebird 연동
### 1. 의존성 추가(gradle kotlin 기준으로 작성)
- build.gradle.kts파일의 dependencies 내에 다음 의존성을 추가합니다.
```kotlin
runtimeOnly("org.firebirdsql.jdbc:jaybird")
``` 

### 2. application.yml 설정
- FirebirdDialect 파일을 직접 생성하여 Dialect 직접적으로 지정할 수 있게 해줍니다.
```java
package com.test.config;

import org.hibernate.dialect.Dialect;

public class FirebirdDialect extends Dialect {

    public FirebirdDialect() {
        super();
    }
}
```

### 3. application.yml 설정
- 다음과 같이 설정하여 Firebird에 접속할 수 있게 해줍니다.
```yml
spring:
  datasource:
    username: tset
    password: test1234
    url: jdbc:firebirdsql://192.168.100.193:3050/test.fdb
    driver-class-name: org.firebirdsql.jdbc.FBDriver
  jpa:
    properties:
      hibernate.dialect: com.test.config.FirebirdDialect
    hibernate:
      ddl-auto: update
```
- 기존에 생성한 firebird db에 접속하기 위한 정보를 입력합니다.
- hibernate.dialect에 직접 생성한 FirebirdDialect를 지정해줍니다.

