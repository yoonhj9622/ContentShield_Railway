# 🚂 Railway 포트폴리오 배포 가이드 (GitHub 연동)

Railway는 GitHub 저장소를 연결하기만 하면 자동으로 빌드와 배포를 수행해주는 서비스입니다. 신용카드 없이 가입 후 처음 제공되는 크레딧으로 충분히 테스트가 가능합니다.

---

## 🛠️ 1단계: 프로젝트 환경 설정 (로컬 수정)

Railway는 포트(Port)를 동적으로 할당하므로, 우리 프로젝트가 이를 인식할 수 있게 아주 살짝 고쳐야 합니다.

### 1. Spring Boot (`backend-springboot/Dockerfile`)
맨 아래 `${PORT:8081}` 설정을 추가하여 Railway가 주는 포트를 우선적으로 사용하게 합니다.
```dockerfile
# 기존 ENTRYPOINT를 아래처럼 수정
ENTRYPOINT ["java", "-Dserver.port=${PORT:8081}", "-jar", "app.jar"]
```

### 2. FastAPI (`backend-fastapi/Dockerfile`)
포트 설정을 변수로 받게 수정합니다.
```dockerfile
# CMD 부분을 아래처럼 수정
CMD ["sh", "-c", "uvicorn main:app --host 0.0.0.0 --port ${PORT:-8000}"]
```

---

## 🚀 2단계: Railway 배포 순서

1.  **Railway 접속 및 로그인**: [railway.app](https://railway.app/)에 접속하여 **`Login with GitHub`**를 선택합니다.
2.  **새 프로젝트 생성**: 
    *   `+ New Project` 버튼 클릭
    *   `Deploy from GitHub repo` 선택
    *   여러분의 `contentshield` 리포지토리 선택
3.  **서비스 개별 설정**:
    *   Railway는 한 리포지토리 안에 여러 폴더가 있으면 어떤 것을 실행할지 묻습니다.
    *   **Backend (Spring)**: Root Directory를 `backend-springboot`로 설정
    *   **AI (FastAPI)**: Root Directory를 `backend-fastapi`로 설정
    *   **Frontend (React)**: Root Directory를 `frontend-react`로 설정
4.  **데이터베이스 추가**:
    *   `+ Add` -> `Database` -> `Add MariaDB` 클릭
    *   생성된 MariaDB의 `Variables` 탭에서 접속 정보를 확인합니다.

---

## 🔑 3단계: 환경 변수 등록 (Variables)

Railway 대시보드 내 각 서비스의 **`Variables`** 탭에 아래 내용을 입력하세요.

*   **Spring Boot 서비스 하단 `Variables`**:
    *   `MYSQLHOST`: MariaDB 서비스의 Host (예: `roundhouse.proxy.rlwy.net`)
    *   `MYSQLPORT`: MariaDB 서비스의 Port (예: `12345`)
    *   `MYSQLUSER`: `root`
    *   `MYSQLPASSWORD`: (MariaDB 비밀번호)
    *   `MYSQLDATABASE`: `sns_content_analyzer`
    *   `AI_SERVICE_URL`: (생성된 AI-FastAPI 서비스의 주소 - `https://xxx.railway.app`)

*   **AI (FastAPI) 서비스 하단 `Variables`**:
    *   `GROQ_API_KEY`: 여러분의 API 키
    *   `MYSQLHOST`, `MYSQLPORT`, `MYSQLPASSWORD` 등 DB 정보도 동일하게 입력 (DB 연동 시 필요)

*   **Frontend (React) 서비스 하단 `Variables`**:
    *   `BACKEND_URL`: (생성된 Spring Boot 주소 - `https://xxx.railway.app`)
    *   `AI_URL`: (생성된 FastAPI 주소 - `https://xxx.railway.app`)

---

## 🏁 4단계: 면접관에게 줄 주소 확인

1.  각 서비스의 **`Settings`** 탭으로 이동합니다.
2.  **`Networking`** 항목에서 `Generate Domain` 버튼을 누르면 `xxx.railway.app` 형태의 고유 주소가 생성됩니다.
3.  이 중 **Frontend의 주소**를 면접관에게 보내주면 됩니다!

> [!IMPORTANT]
> Railway는 GitHub에 푸시(push)할 때마다 자동으로 재배포됩니다. 따라서 로컬에서 작업하고 푸시만 하면 포트폴리오 사이트도 자동으로 최신 상태가 됩니다.
