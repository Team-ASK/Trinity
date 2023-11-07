using UnityEngine;
using Newtonsoft.Json.Linq;

public class VariableManager : MonoBehaviour
{
    // 싱글톤 인스턴스
    public static VariableManager Instance { get; private set; }

    // 게임 내에서 필요한 변수들
    public string playerId;
    public string message;
    public string gameRoomId;
    public bool fertilizerInput;
    public bool fertilizerProduce;
    public bool fertilizerResearch;
    public bool purifierFix;
    public bool co2CollectorFix;
    public bool centralGardenFix;
    public bool taurineProduce;
    public bool asteroidShotDown;
    public bool barrierSystemResearch;


    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
            DontDestroyOnLoad(gameObject);
            playerId = PlayerPrefs.GetString("PlayerId");
        }
        else
        {
            Destroy(gameObject);
        }
    }

    public void SetFirstDay(string receivedMessage)
    {
        JObject jsonObject = JObject.Parse(receivedMessage);
        string type = (string)jsonObject["type"];
        if (type == "firstDay") {
            // 'specific_type'에 따른 처리
            var firstDayData = jsonObject.ToObject<FirstDay>();
        }
        else if (type == "nextDay"){
            ;
        }
    }

    public class FirstDay
    {
        // JSON 데이터 구조에 맞게 필드 정의
        public FirstRoom firstRoom { get; set; }
        public SecondRoom secondRoom { get; set; }
        public ThirdRoom thirdRoom { get; set; }
        public int fertilizerAmount { get; set; }
        public int eventCode { get; set; }
        public bool fertilizerUpgrade { get; set; }
        public bool barrierUpgrade { get; set; }
        public bool conflictAsteroid { get; set; }
    }
    public class FirstRoom
    {
        public int fertilizerAmount { get; set; }
        public string message { get; set; }
        public bool fertilizerStatus { get; set; }
    }

    public class SecondRoom
    {
        public int fertilizerAmount { get; set; }
        public string message { get; set; }
        public bool farmStatus { get; set; }
        public int carbonCaptureTryCount { get; set; }
    }

    public class ThirdRoom
    {
        public int fertilizerAmount { get; set; }
        public string message { get; set; }
        public bool asteroidStatus { get; set; }
    }

}
