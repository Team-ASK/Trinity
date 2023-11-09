using UnityEngine;
using WebSocketSharp;
using System.Collections;
using UnityEngine.Networking;
using UnityEngine.SceneManagement;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using System;
public class WebSocketClientManager : MonoBehaviour
{
    private WebSocket webSocket;
    private bool isConnected;
    private string serverURL = "wss://k9b308.p.ssafy.io/websocket"; // 웹소켓 서버 주소로 바꾸세요
    private string userId; // userId를 저장하는 멤버 변수
    public GameObject ship;
    private string apiUrl = "https://k9b308.p.ssafy.io/api/game/match/"; // 대상 URL로 바꾸세요.


    void Awake(){
        ship = GameObject.Find("spaceship_revert");
        Debug.Log("ship : " + ship);
        
    }
    void Start()
    {
        isConnected = false;
        DontDestroyOnLoad(gameObject);
    }

    public void ToggleWebSocketConnection()
    {
        if (isConnected)
        {
            Debug.Log("웹소켓 연결 종료");
            // 이미 연결되어 있는 경우 연결 종료
            webSocket.Close();
            isConnected = false;
            Debug.Log("WebSocket disconnected");
        
        }
        else
        {
            // userId 초기화
            userId = PlayerPrefs.GetString("UserId"); // PlayerPrefs에서 userId를 가져옴
            Debug.Log("매칭 큐 등록 요청 보내기");
            StartCoroutine(SendGetRequest());
            Debug.Log("웹소켓 연결 시도");
            // 연결되어 있지 않은 경우 연결 시도
            webSocket = new WebSocket(serverURL);
            webSocket.OnOpen += (sender, e) =>
            {
                isConnected = true;
                Debug.Log("WebSocket connected");
                // 연결 후 userId를 서버로 보냅니다.
                Debug.Log("저장된 userId: " + userId);
                SendUserId(userId);
            };
            webSocket.OnError += (sender, e) => Debug.LogError("WebSocket error: " + e.Message);
            webSocket.ConnectAsync();
            webSocket.OnMessage += (sender, e) =>
            {
                // 이 부분에서 웹소켓 서버로부터 수신한 메시지를 처리합니다.
                // e.Data에 수신된 메시지가 포함되어 있습니다.
                string receivedMessage = e.Data;
                Debug.Log("Received message: " + receivedMessage);
                try
                {
                    JObject jsonObject = JObject.Parse(receivedMessage);
                    string type = (string)jsonObject["type"];
                    // 나머지 처리 로직
                    if (type == "firstDay") {
                        // 'specific_type'에 따른 처리
                        Debug.Log("firstDay 메시지 수신");
                        UnityMainThreadDispatcher.Enqueue(() =>
                        {
                            // 이 부분에서 메인 스레드에서 실행하고자 하는 작업을 수행
                            StartCoroutine(ProcessAndLoadScene(receivedMessage));
                        });
                    }
                }
                catch (Exception ex)
                {
                    Debug.LogError("Json Message가 아닙니다 : " + ex.Message);
                }
            };

        }
    }

    IEnumerator ProcessAndLoadScene(string receivedMessage)
    {
        // 메시지 처리
        VariableManager.Instance.SetFirstDayData(receivedMessage);

        // 다른 작업 수행
        Debug.Log("Message processing completed.");

        // 다음 씬으로 전환
        AsyncOperation asyncOp = SceneManager.LoadSceneAsync("GameInitializer");
        asyncOp.allowSceneActivation = false;

        while (!asyncOp.isDone)
        {
            // 필요에 따라 로딩 상태를 모니터링하거나 처리
            float progress = Mathf.Clamp01(asyncOp.progress / 0.9f);
            Debug.Log("Loading progress: " + (progress * 100) + "%");

            // 예를 들어, 특정 조건이 충족되면 씬을 활성화
            if (progress >= 0.9f)
            {
                asyncOp.allowSceneActivation = true;
            }

            yield return null; // 다음 프레임까지 대기
        }
    }


    private void SendUserId(string userId)
    {
        // userId를 서버로 보내는 로직을 구현하세요.
        // 이 부분에서 웹소켓 메시지를 생성하고 서버에 전송합니다.
        // 예를 들어, JSON 형식으로 메시지를 만들어 보낼 수 있습니다.
        string jsonMessage = "{\"userId\": \"" + userId + "\", \"type\": \"matching\"}";
        Debug.Log("웹소켓 전송할 jsonMessage:" + jsonMessage);
        webSocket.Send(jsonMessage);
    }

    public void SendRoundEnd() {
        // 객체 생성과 데이터 채우기
        RoundEndData data = new RoundEndData();

        if (VariableManager.Instance.roomNo == 1) {
            data.type = "roundEnd"; // 또는 다른 타입에 맞게 설정
            data.gameRoomId = VariableManager.Instance.gameRoomId;
            data.roomNum = "first"; // 또는 다른 방 번호에 맞게 설정

            // FirstRoomPlayerRequestDto 채우기
            data.FirstRoomPlayerRequestDto = new FirstRoomPlayerRequestDto
            {
                userId = VariableManager.Instance.playerId,
                gameRoomId = VariableManager.Instance.gameRoomId,
                roomNo = "first", // 또는 다른 방 번호에 맞게 설정
                message = VariableManager.Instance.message, // 원하는 메시지 설정
                inputFertilizerTry = VariableManager.Instance.inputFertilizerTry,
                makeFertilizerTry = VariableManager.Instance.makeFertilizerTry,
                fertilizerUpgradeTry = VariableManager.Instance.fertilizerUpgradeTry,
                purifierTry = VariableManager.Instance.purifierTry
            };
        } else if (VariableManager.Instance.roomNo == 2) {
            data.type = "roundEnd"; // 또는 다른 타입에 맞게 설정
            data.gameRoomId = VariableManager.Instance.gameRoomId;
            data.roomNum = "second"; // 또는 다른 방 번호에 맞게 설정

            // SecondRoomPlayerRequestDto 채우기
            data.SecondRoomPlayerRequestDto = new SecondRoomPlayerRequestDto
            {
                userId = VariableManager.Instance.playerId,
                gameRoomId = VariableManager.Instance.gameRoomId,
                roomNo = "second", // 또는 다른 방 번호에 맞게 설정
                message = VariableManager.Instance.message, // 원하는 메시지 설정
                InputFertilizerTry = VariableManager.Instance.inputFertilizerTry,
                makeFertilizerTry = VariableManager.Instance.makeFertilizerTry,
                carbonCaptureTry = VariableManager.Instance.carbonCaptureTry,
                farmTry = VariableManager.Instance.farmTry,
                taurineFilterTry = VariableManager.Instance.taurineFilterTry
            };
        } else if (VariableManager.Instance.roomNo == 3) {
            data.type = "roundEnd"; // 또는 다른 타입에 맞게 설정
            data.gameRoomId = VariableManager.Instance.gameRoomId;
            data.roomNum = "third"; // 또는 다른 방 번호에 맞게 설정

            // ThirdRoomPlayerRequestDto 채우기
            data.ThirdRoomPlayerRequestDto = new ThirdRoomPlayerRequestDto
            {
                userId = VariableManager.Instance.playerId,
                gameRoomId = VariableManager.Instance.gameRoomId,
                roomNo = "third", // 또는 다른 방 번호에 맞게 설정
                message = VariableManager.Instance.message, // 원하는 메시지 설정
                inputFertilizerTry = VariableManager.Instance.inputFertilizerTry,
                makeFertilizerTry = VariableManager.Instance.makeFertilizerTry,
                asteroidDestroyTry = VariableManager.Instance.asteroidDestroyTry,
                barrierDevTry = VariableManager.Instance.barrierDevTry,
                asteroidStatus = VariableManager.Instance.asteroidStatus
            };
        }

        // JSON 직렬화 설정
        JsonSerializerSettings settings = new JsonSerializerSettings
        {
            NullValueHandling = NullValueHandling.Ignore
        };

        // 객체를 JSON 문자열로 직렬화
        string jsonMessage = JsonConvert.SerializeObject(data, settings);
        Debug.Log("웹소켓 전송할 jsonMessage:" + jsonMessage);
        webSocket.Send(jsonMessage);
    }

    public class FirstRoomPlayerRequestDto
    {
        public string userId { get; set; }
        public string gameRoomId { get; set; }
        public string roomNo { get; set; }
        public string message { get; set; }
        public bool inputFertilizerTry { get; set; }
        public bool makeFertilizerTry { get; set; }
        public bool fertilizerUpgradeTry { get; set; }
        public bool purifierTry { get; set; }
    }

    public class SecondRoomPlayerRequestDto
    {
        public string userId { get; set; }
        public string message { get; set; }
        public string gameRoomId { get; set; }
        public string roomNo { get; set; }
        public bool InputFertilizerTry { get; set; }
        public bool makeFertilizerTry { get; set; }
        public bool carbonCaptureTry { get; set; }
        public bool farmTry { get; set; }
        public bool taurineFilterTry { get; set; }
    }

    public class ThirdRoomPlayerRequestDto
    {
        public string userId { get; set; }
        public string gameRoomId { get; set; }
        public string roomNo { get; set; }
        public string message { get; set; }
        public bool inputFertilizerTry { get; set; }
        public bool makeFertilizerTry { get; set; }
        public bool asteroidDestroyTry { get; set; }
        public bool barrierDevTry { get; set; }
        public bool asteroidStatus { get; set; }
    }


    public class RoundEndData
    {
        public string type { get; set; }
        public string gameRoomId { get; set; }
        public string roomNum { get; set; }
        public FirstRoomPlayerRequestDto FirstRoomPlayerRequestDto { get; set; }
        public SecondRoomPlayerRequestDto SecondRoomPlayerRequestDto { get; set; }
        public ThirdRoomPlayerRequestDto ThirdRoomPlayerRequestDto { get; set; }
    }


    // private void shipLog()
    // {
    //     Debug.Log(ship);
    // }

    IEnumerator SendGetRequest()
    {
        Debug.Log("webRequest 송신");
        using (UnityWebRequest webRequest = UnityWebRequest.Get(apiUrl+userId))
        {
            Debug.Log("요청 보내는 url:"+apiUrl+userId);
            // 요청을 보냅니다.
            yield return webRequest.SendWebRequest();
            if (webRequest.result == UnityWebRequest.Result.Success)
            {
                // 요청이 성공했을 때의 처리
                Debug.Log("HttpRequest successful");
            }
            else
            {
                // 요청이 실패했을 때의 처리
                Debug.LogError("HttpRequest failed: " + webRequest.error);
            }
        }
    }
}
