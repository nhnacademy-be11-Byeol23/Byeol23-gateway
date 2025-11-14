# Byeol23-gateway

- server port : 10336, 10337


### GatewayFilter

	GatewayFilter는 게이트웨이에서 URI를 지정해줄 수 있다.
	api/books의 요청 같은 경우에는 jwt토큰 검증을 할 필요가 없기 때문에
	GatewayFilter로 URI를 지정해서 빼준다.

### 작동 순서   
클라이언트 요청  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
↓  
Global Filter (전역 필터)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
↓  
Route Predicate 체크 (어떤 서비스로 보낼지 결정)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
↓  
Gateway Filter (해당 라우트에 등록된 필터 실행)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
↓  
Backend Service 호출  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
↓  
Gateway Filter Post 처리  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
↓  
Global Filter Post 처리  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
↓  
클라이언트 응답  