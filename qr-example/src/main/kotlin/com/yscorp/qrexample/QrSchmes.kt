package com.yscorp.qrexample

class QrSchmes {
}
/*
### 1. **URL 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 자동으로 웹 페이지로 이동합니다.
- **예시:**
  ```plaintext
  https://www.example.com
  ```
  - 사용자가 스캔하면 `https://www.example.com` 웹사이트로 이동합니다.

### 2. **전화 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 자동으로 전화 걸기 화면이 열립니다.
- **예시:**
  ```plaintext
  tel:+1234567890
  ```
  - 사용자가 스캔하면 `+1234567890` 번호로 전화를 걸기 위한 다이얼러가 열립니다.

### 3. **SMS 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 문자 메시지 앱이 열리고, 미리 지정된 번호와 메시지가 입력됩니다.
- **예시:**
  ```plaintext
  sms:+1234567890?body=Hello%20World
  ```
  - 사용자가 스캔하면 `+1234567890` 번호로 "Hello World"라는 내용이 입력된 SMS 작성 화면이 열립니다.

### 4. **메일 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 이메일 앱이 열리고, 미리 지정된 수신자, 제목, 내용이 입력된 이메일 작성 화면이 나타납니다.
- **예시:**
  ```plaintext
  mailto:example@example.com?subject=Hello&body=This%20is%20a%20test
  ```
  - 사용자가 스캔하면 `example@example.com` 주소로 "Hello"라는 제목과 "This is a test"라는 내용이 입력된 이메일 작성 화면이 열립니다.

### 5. **Wi-Fi 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 Wi-Fi 네트워크에 자동으로 연결할 수 있습니다.
- **예시:**
  ```plaintext
  WIFI:T:WPA;S:MyNetworkSSID;P:MyPassword;;
  ```
  - 사용자가 스캔하면 `MyNetworkSSID`라는 이름의 Wi-Fi 네트워크에 `MyPassword` 비밀번호로 연결됩니다.

### 6. **지도 위치 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 지도 앱이 열리고 특정 위치가 표시됩니다.
- **예시:**
  ```plaintext
  geo:37.7749,-122.4194
  ```
  - 사용자가 스캔하면 위도 `37.7749`, 경도 `-122.4194`의 위치가 지도 앱에 표시됩니다 (이 예시는 샌프란시스코, 미국의 위치입니다).

### 7. **vCard (연락처) 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 연락처 정보가 표시되고, 이를 스마트폰에 저장할 수 있습니다.
- **예시:**
  ```plaintext
  BEGIN:VCARD
  VERSION:3.0
  FN:John Doe
  ORG:Example Company
  TEL:+1234567890
  EMAIL:john.doe@example.com
  END:VCARD
  ```
  - 사용자가 스캔하면 "John Doe"라는 이름의 연락처 정보가 스마트폰에 저장할 수 있도록 표시됩니다.

### 8. **메시징 앱 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 특정 메시징 앱으로 채팅창이 열립니다. (WhatsApp을 예로 들면)
- **예시:**
  ```plaintext
  whatsapp://send?phone=+1234567890&text=Hello%20World
  ```
  - 사용자가 스캔하면 WhatsApp에서 `+1234567890` 번호로 "Hello World"라는 메시지를 보낼 수 있는 채팅창이 열립니다.

### 9. **앱 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 특정 앱을 열거나, 설치를 유도합니다. (앱 설치 페이지로 이동할 수도 있습니다.)
- **예시 (App Store):**
  ```plaintext
  itms-apps://itunes.apple.com/app/id123456789
  ```
  - 사용자가 스캔하면 특정 앱의 App Store 페이지로 이동합니다.
- **예시 (Google Play):**
  ```plaintext
  market://details?id=com.example.app
  ```
  - 사용자가 스캔하면 특정 앱의 Google Play Store 페이지로 이동합니다.

### 10. **QR 코드를 통해 인코딩된 텍스트**

- **설명:** 단순한 텍스트 메시지를 QR 코드에 포함시킬 수 있습니다. 사용자가 스캔하면 해당 텍스트가 표시됩니다.
- **예시:**
  ```plaintext
  Hello, this is a QR code with a simple text message.
  ```
  - 사용자가 스캔하면 이 텍스트 메시지가 화면에 표시됩니다.

### 11. **이벤트 초대 (iCalendar) 스킴**

- **설명:** 사용자가 QR 코드를 스캔하면 캘린더 앱에 이벤트가 추가됩니다.
- **예시:**
  ```plaintext
  BEGIN:VEVENT
  SUMMARY:Meeting
  DTSTART:20230815T090000Z
  DTEND:20230815T100000Z
  LOCATION:Conference Room
  DESCRIPTION:Discussion on new project
  END:VEVENT
  ```
  - 사용자가 스캔하면 이벤트가 캘린더에 추가됩니다.

 */