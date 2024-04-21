from openai import OpenAI
import sys
import json

class ChatWithMemory:
    def __init__(self, api_key): # 생성자
        self.client = OpenAI(api_key=api_key)

    def send_message(self, messages): # 메시지 전송 + 대답 리턴
        # 답변 받기
        response = self.client.chat.completions.create(
            model="gpt-3.5-turbo", # 모델 정의
            # model = "ft:gpt-3.5-turbo-0613:personal:butler-3:9Ew4gl0F", # 얘가 우리 등신같은 모델
            messages=messages, # 메시지 기록 + 새로운 메시지
        )
        return response.choices[0].message.content # 대답 리턴
        # 첫 번째 응답이 보통 가장 정확하기 때문에 첫 번째 리턴

if __name__ == "__main__": # 파일 실행되면 무조건 실행
    api_key = "my-api-key"
    messages = json.loads(sys.argv[1])
    chat_with_memory = ChatWithMemory(api_key) # ChatWithMemory 클래스 생성
    response = chat_with_memory.send_message(messages) # 채팅 실행
    print(response)