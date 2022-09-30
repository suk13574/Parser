package plan;

import java.io.*;

public class Parser {

	int token;
	int value;
	int ch;

	private PushbackInputStream input;
	final int NUMBER = 256;

	Parser(PushbackInputStream is) {
		input = is;
	}

	int getToken() {
		while (true) {
			try {
				ch = input.read(); // ���� �ϳ��� �д´�. (ex. ���� 12��� 1�� ����)
				// ���� ���� �Ʒ� ���� �ϳ���� �ѱ��.
				if (ch == ' ' || ch == '\t' || ch == '\r') ;
				
				// isDigit �޼��带 ���� �������� �˾Ƴ���.
				else if (Character.isDigit(ch)) {
					value = number();
					input.unread(ch); //�о��� �κ��� �ǵ��� (���� ���� �� ���� �ǵ���)
					return NUMBER; // ���� ���� ���� ���ڶ�� ��� ��ȯ
				}
				// ch�� ' ', '\t', '\r' �� �ƴϰ� ���ڵ� �ƴϸ� �״�� ��ȯ�Ѵ�.
				else return ch; 
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	void match(int c) {
		if (token == c) // ���� ��ū�� ���� ��ū�� ���ٸ� getToken �޼��� ȣ��
			token = getToken();
		else error();
	}
	
	/* <expr> -> term { + <term> | '-' <term> }
	 * ���� �Ǵ� ������ ���� �Ľ��ϰ� ����Ѵ�.
	 * 1. ��ū ���� '+'�̰ų� '-'�̸� �Ʒ��� �����Ѵ�.
	 * 1-(1). ��ū �� '+' -> ��ū Ȯ�� ��(match) ���� ������ �����Ͽ� result�� �ִ´�.
	 * 1-(2). ��ū �� '-' -> ��� Ȯ�� ��(match) ���� ������ �����Ͽ� result�� �ִ´�.
	 * 2. ���� ����� ��ȯ�Ѵ�.
	 */
	int expr() {
		int result = term();
		while(token == '+' || token == '-') {
			if(token == '+') {
			match('+');
			result += term();
			}
			else if(token == '-') {
				match('-');
				result -= term();
			}
		}
		
		return result;
	}
	
	/* <term> -> <factor> { '*' <fator> | '/' <factor> }
	 * ���� �Ǵ� ������ ���� ���� �Ľ��ϰ� ����Ѵ�.
	 * 1. ��ū ���� '+'�̰ų� '-'�̸� �Ʒ��� �����Ѵ�.
	 * 1-(1). ��ū �� '*' -> ��ū Ȯ�� ��(match) ���� ������ �����Ͽ� result�� �ִ´�.
	 * 1-(2). ��ū �� '/' -> ��ū Ȯ�� ��(match) ������ ������ �����Ͽ� result�� �ִ´�.
	 * 2. ���� ����� ��ȯ�Ѵ�.
	 */
	int term() {
		int result = factor();
		while(token == '*' | token == '/') {
			if (token == '*') {
				match('*');
				result *= factor();
			} 
			else if (token == '/') {
				match('/');
				result /= factor();
			}
		}
		return result;
	}
	
	/* <factor> -> ( <expr> ) | number
	 * �� ���� ��쿡 �����Ͽ� ��ȯ�Ѵ�.
	 * 1. '(' ��� -> �Ұ�ȣ�� ���� �����Ѵ�
	 * 2. ���� ��� -> ���ڸ� �����Ѵ�.
	 */
	int factor() {
		int result = 0;
		// 1. '(' ��ū
		if(token == '(') {
			match('(');
			result = expr();
			match(')');
		}
		// 2. NUMBER ��ū
		else if(token == NUMBER) {
			result = value; // ���� ����� �־��ش�.
			match(NUMBER); 
		}
		return result;
	}
	
	/* command -> <expr> '\n'
	 * 1. expr �޼��� ȣ��
	 * 2. ���� ������ '\n' ��ū ������ ����� ���
	 */
	void command() {
		int result = expr();
		// ���� ���� ��ū�� �ٹٱ� ��ū�̶�� ��� ����Ѵ�.
		if(token == '\n') {
			System.out.printf("The result is: %d\n", result);
		}
	}
	
	/* ó�� �����ϴ� �Լ�
	 * ��ū�� �������� command�� ȣ��
	 */
	void parse() {
		token = getToken();
		command();
	}
	
	/* number -> digit { digit }
	 * ĳ������ ���� int ������ �ٲٱ�
	 * ex. 12��ȯ
	 * 1) ch = '1';
	 * 2) result = 1;
	 * 3) ch = '2';
	 * 4) result = 10 * 1 + 2;
	 * 5) ch = '+';
	 * 6) result = 12 ��ȯ
	 */
	int number() {
		int result = ch - '0'; // ���� ���ڷ� �ٲ۴�.
		try {
			ch = input.read(); //���� ���ڸ� �а�, ���ڶ�� �ݺ�
			while(Character.isDigit(ch)) {
				//�տ� �ִ� ������ �ڸ��� �ϳ� Ű���. -> 10 * result
				result = 10 * result + ch - '0';
				ch = input.read(); //���� ���� ����
			}
		} catch(IOException e) {
			System.err.println(e);
		}
		return result; //���� result �� ��ȯ (����)
	}
	
	/* error �Լ�
	 * �Ľ̿� ������ ������ ������ ��� �� ����
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