using UnityEngine;
using UnityEngine.UI;
using TMPro;
using UnityEngine.Networking; // UnityWebRequest 사용을 위한 네임스페이스
using System.Collections;

public class TouchToStartGame : MonoBehaviour
{
    public TextMeshProUGUI textObject; // 화면에 보이는 텍스트 Text 컴포넌트
    public Button button1; // 버튼 1
    public Button button2; // 버튼 2
    private bool hasStarted = false;
    
    GameObject ship;
    private string apiUrl = "https://k9b308.p.ssafy.io/api/game"; // 대상 URL로 바꾸세요.

    void Awake()
    {
        // 게임 시작 시 버튼2를 비활성화
        button2.gameObject.SetActive(false);
        ship = GameObject.Find("spaceship_revert");
    }

    void Update()
    {
        // 게임이 시작되지 않았고 터치 입력을 확인 (모바일 터치 또는 데스크톱 클릭)
        if (!hasStarted && (Input.GetMouseButtonDown(0) || (Input.touchCount > 0 && Input.GetTouch(0).phase == TouchPhase.Began)))
        {
            hasStarted = true; // 게임 시작 플래그 설정

            // 텍스트를 비활성화
            textObject.gameObject.SetActive(false);

            // 버튼 1을 비활성화
            button1.gameObject.SetActive(true);

            // 버튼 2를 활성화
            button2.gameObject.SetActive(true);

            // HTTP GET 요청 보내기
            StartCoroutine(SendGetRequest());
        }
    }

    IEnumerator SendGetRequest()
    {
        using (UnityWebRequest webRequest = UnityWebRequest.Get(apiUrl))
        {
            // 요청을 보냅니다.
            yield return webRequest.SendWebRequest();

            if (webRequest.result == UnityWebRequest.Result.ConnectionError || webRequest.result == UnityWebRequest.Result.ProtocolError)
            {
                Debug.LogError("Error: " + webRequest.error);
            }
            else
            {
                // 요청이 성공적으로 완료됐을 때 처리할 내용을 여기에 추가합니다.
                string response = webRequest.downloadHandler.text;
                Debug.Log("Response: " + response);
                // 서버 응답에서 userId 추출
                var responseData = JsonUtility.FromJson<userIdResponseData>(response);
                string userId = responseData.userId;

                // userId를 PlayerPrefs에 저장
                PlayerPrefs.SetString("UserId", userId);

                // PlayerPrefs 값 저장
                PlayerPrefs.Save();

                string loadedValue = PlayerPrefs.GetString("UserId"); // "YourKey"는 확인하려는 PlayerPrefs 키입니다.
                Debug.Log("저장된 userId: " + loadedValue);
                ship.GetComponent<ShipMove>().StartMove();
            }
        }
    }
}
