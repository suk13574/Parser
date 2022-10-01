package plan;

import java.io.*;

public class Parser {

	int token;
	int value;
	int ch;

	private PushbackInputStream input;
	final int NUMBER = 256;
	final int TRUE = 255;
	final int FALSE = 254;

	Parser(PushbackInputStream is) {
		input = is;
	}

	int getToken() {
		while (true) {
			try {
				ch = input.read(); // 다음 하나를 읽는다. (ex. 숫자 12라면 1를 읽음)
				// 읽은 값이 아래 셋중 하나라면 넘긴다.
				if (ch == ' ' || ch == '\t' || ch == '\r') ;
				
				// isDigit 메서드를 통해 숫자인지 알아낸다.
				else if (Character.isDigit(ch)) {
					value = number();
					input.unread(ch); //읽었던 부분을 되돌림 (숫자 읽은 것 까지 되돌림)
					return NUMBER; // 현재 읽은 것이 숫자라는 토근 반환
				}
				// ch가 ' ', '\t', '\r' 이 아니고 숫자도 아니면 그대로 반환한다.
				else return ch; 
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	void match(int c) {
		if (token == c) // 현재 토큰과 받은 토큰이 같다면 getToken 메서드 호출
			token = getToken();
		else error();
	}
	
	/* <expr> 변경
	 * <expr> -> <bexp> {& <bexp> | '|' <bexp>} | !<expr> | true | false
	 * 
	 */
	int expr() {
		int result = bexp();
		if(token == '&' | token == '|'){
			while(token == '&' | token == '|') {
				if(token == '&') {
					
				}
				else if(token == '|') {
					
				}
			}
		}
		else if(token == '!') {
			
		}
		else if(token == TRUE) {
			
		}
		else if(token == FALSE) {
			
		}
		return result;
	}
	
	/* 
	 * <bexp> -> <aexp> [(== | != | < | > | <= | >=) <aexp>]
	 * 
	 */
	int bexp() {
		int result = aexp();
		
		if(token == '=') {
			match('='); //토큰 매칭 시키고 다음 토큰 읽기
			match('='); //다음 토큰 '='이어야 함
			
		}
		else if(token == '!') {
			match('!'); //토큰 매칭 시키고 다음 토큰 읽기
			match('='); //다음 토큰 '='이어야 함
		}
		else if(token == '<') {
			match('<'); //토큰 매칭 시키고 다음 토큰 읽기
			
			// '<=' 연산자
			if(token == '=') {
				match('=');
			}
			// '<' 연산자
			else {
				
			}
			
		}
		else if(token == '>') {
			match('>'); //토큰 매칭 시키고 다음 토큰 읽기
			
			// '>=' 연산자
			if(token == '=') {
				match('=');
			}
			// '>' 연산자
			else {
				
			}
		}
		return result;
	}
	
	/* 완료
	 * <aexp> -> <term> {+ <term> | - <term>}
	 * 덧셈 또는 뺄셈 연산에 대해 파싱하고 계산한다
	 * 1. 토큰 값이 '+'이거나 '-'이면 아래를 수행한다.
	 * 1-(1). 토큰 값 '+' -> 토큰 확인 후(match) 덧셈 연산을 수행하여 result에 넣는다.
	 * 1-(2). 토큰 값 '-' -> 토큰 확인 후(match) 뺼셈 연산을 수행하여 result에 넣는다.
	 * 2. 연산 결과를 반환한다.
	 */
	int aexp() {
		int result = term();
		while(token == '+' | token == '-') {
			if(token == '+') {
				match('+');
				result += term();
			}
			else if(token =='-') {
				match('-');
				result -= term();
			}
		}
		return result;
	}
	
	/* 완료
	 * <term> -> <factor> { '*' <fator> | '/' <factor> }
	 * 곱셈 또는 나눗셈 연산 대해 파싱하고 계산한다.
	 * 1. 토큰 값이 '*'이거나 '/'이면 아래를 수행한다.
	 * 1-(1). 토큰 값 '*' -> 토큰 확인 후(match) 곱셈 연산을 수행하여 result에 넣는다.
	 * 1-(2). 토큰 값 '/' -> 토큰 확인 후(match) 나눗셈 연산을 수행하여 result에 넣는다.
	 * 2. 연산 결과를 반환한다.
	 */
	int term() {
		int result = factor();
		//1. 토큰 값 '*'또는 '/' 확인
		while(token == '*' | token == '/') {
			//1-(1). '*' 토큰
			if (token == '*') {
				match('*');
				result *= factor();
			} 
			//1-(2). '/' 토큰
			else if (token == '/') {
				match('/');
				result /= factor();
			}
		}
		return result;
	}
	
	/* 완료
	 * <factor> -> <number> | '('<aexp>')'
	 * 두 가지 경우에 대응하여 반환한다.
	 * 1. 숫자 토근 -> 숫자를 리턴한다.
	 * 2. '(' 토근 -> 소괄호로 묶어 리턴한다
	 */
	int factor() {
		int result = 0;
		//1.NUMBER 토큰
		if(token == NUMBER) {
			result = value; //숫자 읽음
			match(NUMBER); //토큰 매치 시키고 다음 토큰 읽음
		}
		//2.'(' 토큰
		else if(token == '(') {
			match('(');
			result = aexp();
			match(')');
		}
		return result;
	}
	
	/* 
	 * command -> <expr> '\n'
	 * 1. expr 메서드 호출
	 * 2. 연산 끝나고 '\n' 토큰 받으면 결과값 출력
	 */
	void command() {
		int result = expr();
		// 현재 받은 토큰이 줄바굼 토큰이라면 결과 출력한다.
		if(token == '\n') {
			System.out.printf("The result is: %d\n", result);
		}
	}
	
	/* 
	 * 처음 시작하는 함수
	 * 토큰을 가져오고 command를 호출
	 */
	void parse() {
		token = getToken();
		command();
	}
	
	/* 완료
	 * number -> digit { digit }
	 * 캐릭터형 숫자 int 형으로 바꾸기
	 * ex. 12반환
	 * 1) ch = '1';
	 * 2) result = 1;
	 * 3) ch = '2';
	 * 4) result = 10 * 1 + 2;
	 * 5) ch = '+';
	 * 6) result = 12 반환
	 */
	int number() {
		int result = ch - '0'; // 실제 숫자로 바꾼다.
		try {
			ch = input.read(); //다음 문자를 읽고, 숫자라면 반복
			while(Character.isDigit(ch)) {
				//앞에 있는 숫자의 자릿수 하나 키운다. -> 10 * result
				result = 10 * result + ch - '0';
				ch = input.read(); //다음 문자 읽음
			}
		} catch(IOException e) {
			System.err.println(e);
		}
		return result; //현재 result 값 반환 (숫자)
	}
	
	/* 
	 * error 함수
	 * 파싱에 문제가 있으면 오류문 출력 후 종료
	 */
	void error() {
		System.out.printf("parse error : %d\n", ch);
		System.exit(1);
	}
	
	public static void main(String[] args) {
		Parser p = new Parser(new PushbackInputStream(System.in));
		while(true) {
			System.out.printf(">> ");
			p.parse();
		}
	}

}