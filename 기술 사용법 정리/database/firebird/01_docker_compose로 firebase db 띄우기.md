## 순서
### 1. Firebird 공식 docker 이미지 다운
- 해당 내용은 생략 가능하며 생략하는 경우 2번 단계부터 진행하면 됩니다.
- 설치를 진행할 docker 가 설치된 서버로 이동합니다.
- 다음 명령어로 pull을 진행합니다.
```bash
docker pull jacobalberty/firebird:3.0
```

### 2. docker-compose를 실행하기 위한 docker-compose.yml 파일 생성
- 다음과 같이 docker-compose.yml 파일을 작성합니다.
```yml
version: "3.3"
services:
  firebird:
    image: jacobalberty/firebird:3.0
    container_name: firebird
    environment:
      ISC_PASSWORD: "test1234"
      FIREBIRD_USER: "test"
      FIREBIRD_PASSWORD: "test1234"
      FIREBIRD_DATABASE: "test.fdb"
    ports:
      - "3050:3050"
    volumes:
      - firebird_data:/firebird/data
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

## 추가 - Firebird 모드 변경
- 해당 내용은 필요에 따라 진행하면 됩니다.
- Firebird의 모드를 Super, SuperClassic, Classic 중 하나로 변경할 수 있습니다.
- 해당 내용은 firebird docker에서 환경변수로 지원하지 않아 config 파일을 volume 으로 지정 하 config 설정 변경 후 다시 docker-compose를 실행해야 합니다.

### 1. firebird config volume으로 설정
- 다음과 같이 docker-compose.yml 파일을 작성합니다.
- 기존 내용에서 firebird_conf 관련 내용이 추가되었습니다.
```yml
version: "3.3"
services:
  firebird:
    image: jacobalberty/firebird:3.0
    container_name: firebird
    environment:
      ISC_PASSWORD: "test1234"
      FIREBIRD_USER: "test"
      FIREBIRD_PASSWORD: "test1234"
      FIREBIRD_DATABASE: "test.fdb"
    ports:
      - "3050:3050"
    volumes:
      - firebird_data:/firebird/data
      - firebird_conf:/firebird/etc
volumes:
  firebird_data:
  firebird_conf:
```

### 2. docker-compose 실행 후 config 변경
- docker-compose를 실행 후 다음 명령어로 docker 내부 bash 에 진입 합니다.
```bash
docker exec -it firebird bash
```
- 다음 명령어로 config 파일을 확인합니다.
```bash
cat /firebird/etc/firebird.conf
```
- 다음 명령어로 config 파일을 수정합니다.
```bash
echo "ServerMode = SuperClassic" >> /firebird/etc/firebird.conf
```
- 위 내용은 ServerMode를 결정하는 것으로 Super, SuperClassic, Classic 중 하나를 선택하여 입력합니다.
- 만약 `기존에 설정해둔 ServerMode`가 있다면 삭제를 위해서 해당 명령어를 입력합니다.
```bash
sed -i '/ServerMode = SuperClassic/d' /firebird/etc/firebird.conf
```

### 3. docker-compose 재실행
- 다음 명령어로 docker-compose를 재실행합니다.
```bash
docker-compose down
docker-compose up -d
```