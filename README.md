# zero-base-cms

개요 : 간단한 커머스 프로젝트

Goal : 셀러와 구매자 사이를 중개해 주는 커머스 서버를 구축한다.


## 활용 기술
Spring, Jpa, Mysql, Redis, Docker, AWS


## 회원
### 공통
- [x] 이메일을 통해서 인증번호를 통한 회원가입

### 고객
- [x]  회원 가입
- [x]  인증 ( 이메일 )
- [x]  로그인 토큰 발행
- [x]  로그인 토큰을 통한 제어 확인 (JWT, Filter를 사용해서 간략하게 )  
- [x]  예치금 관리

### 셀러
- [x]  회원가입


## 주문

### 셀러
- [x] 상품 등록, 수정
- [x] 상품 삭제

### 구매자
- [x] 장바구니를 위한 Redis 연동
- [x] 상품 검색 & 상세 페이지
- [x] 장바구니에 물건 추가
- [x] 장바구니 확인
- [x] 장바구니 변경
- [x] 주문하기 - 1. 잔액확인 2. 상품 품절/금액 변동 확인
- [x] 주문내역 이메일로 발송하기


## feedback

1. SignUpApplication -> customerSignUp, sellerSignUp 함수에서 메일과 제목등은 프로퍼티나 다른 컨피그레이션 파일로 빼거나 db에 저장된 값을 가져오도록 처리하기
2. getVerificationEmailBody 함수에 있는 메일 인증 폼등은 보통 DB나 html템플릿을 통해서 보통 처리
3. 메일 인증하는 부분에서 코드와 이메일을 통해서 인증을 하는데, 인증받을 때는 굳이 메일 정보를 노출시킬 필요는 없음.
코드부분의 값을 좀더 늘려서, 코드로만 인증을 받게 처리하심이 좀더 좋은 방향
3. 필터나 컨트롤러에서 사용하는 ""X-AUTH-TOKEN"" 문자열에 대해서는 따로 상수로 빼서 처리하시면 변경시 용이
4. 리턴하는데 사용되는 부분이 따로 없으면 void로 리턴
5. 도메인에서 한번 초기화된 변수는 final로 상수 처리
6. 함수명을 부여할 때, 서비스에 이미 의미가 포함되어 있으면 함수명에 굳이 중복해서 이름을 넣지 않아도 됨.
