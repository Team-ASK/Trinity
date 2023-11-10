using UnityEngine;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;

public class VariableManager : MonoBehaviour
{
    // 싱글톤 인스턴스
    public static VariableManager Instance { get; private set; }

    // 게임 내에서 필요한 변수들
    // 게임 내내 고정
    public string gameRoomId;
    public string playerId;
    // 개인이 방에 관계없이 가지고 있으며 상호작용으로 바뀌는 변수 
    public string message;
    public int power;
    public int hasTaurine;
    public bool usedTaurine;
    public int workLimit;
    // 우주선 정보 (3명 공통)
    public int fertilizerAmount;
    public int eventCode;
    public bool conflictAsteroid;
    public int date;
    public int foodAmount;
    public int morningEvent;  // 1:우주멀미, 2:생일, 3:둘다 (비상식량이벤트는 date=10일때로 수행)
    // 방 번호 및 방 정보
    public int roomNo;
    public int fertilizerAmountInRoom;
    // 방 공통된 행동
    public bool inputFertilizerTry;
    public bool makeFertilizerTry;
    // 1번 비료방 정보 및 행동
    public bool purifierStatus = true;
    public bool fertilizerUpgrade;
    public bool fertilizerUpgradeTry;
    public bool purifierTry;
    // 2번 타우린방 정보 및 행동
    public bool farmStatus = true;
    public int carbonCaptureStatus;
    public int carbonCaptureTryCount;
    public bool carbonCaptureTry;
    public bool taurineFilterTry;
    public bool farmTry;
    // 3번 관제실 정보 및 행동
    public bool asteroidStatus;
    public bool asteroidDestroyTry;
    public bool barrierDevTry;
    public bool barrierUpgrade;
    public bool blackHoleObserved;
    public bool observerTry; 

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

    public void SetFirstDayData(string receivedMessage)
    {
        DayData firstDayData = JsonConvert.DeserializeObject<DayData>(receivedMessage);
        // 여기서 필요한 변수에 데이터 할당
        fertilizerAmount = firstDayData.fertilizerAmount;
        eventCode = firstDayData.eventCode;
        fertilizerUpgrade = firstDayData.fertilizerUpgrade;
        barrierUpgrade = firstDayData.barrierUpgrade;
        conflictAsteroid = firstDayData.conflictAsteroid;
        gameRoomId = firstDayData.gameRoomId;
        foodAmount = firstDayData.foodAmount;
        if (firstDayData.firstResponseDto != null) {
            roomNo = 1;
            purifierStatus = firstDayData.firstResponseDto.purifierStatus;
            fertilizerAmountInRoom = firstDayData.firstResponseDto.fertilizerAmount;
            message = firstDayData.firstResponseDto.message;
        } else if (firstDayData.secondResponseDto != null) {
            roomNo = 2;
            farmStatus = firstDayData.secondResponseDto.farmStatus;
            carbonCaptureStatus = firstDayData.secondResponseDto.carbonCaptureStatus;
            carbonCaptureTryCount = firstDayData.secondResponseDto.carbonCaptureTryCount;
            fertilizerAmountInRoom = firstDayData.secondResponseDto.fertilizerAmount;
            message = firstDayData.secondResponseDto.message;
        } else {
            roomNo = 3;
            fertilizerAmountInRoom = firstDayData.thirdResponseDto.fertilizerAmount;
            message = firstDayData.thirdResponseDto.message;
        }
    }

    public void SetNextDayData(string receivedMessage)
    {
        DayData nextDayData = JsonConvert.DeserializeObject<DayData>(receivedMessage);
        InitializeAction();
        // 여기서 필요한 변수에 데이터 할당
        fertilizerAmount = nextDayData.fertilizerAmount;
        eventCode = nextDayData.eventCode;
        fertilizerUpgrade = nextDayData.fertilizerUpgrade;
        barrierUpgrade = nextDayData.barrierUpgrade;
        conflictAsteroid = nextDayData.conflictAsteroid;
        gameRoomId = nextDayData.gameRoomId;
        foodAmount = nextDayData.foodAmount;
        if (nextDayData.firstResponseDto != null) {
            roomNo = 1;
            purifierStatus = nextDayData.firstResponseDto.purifierStatus;
            fertilizerAmountInRoom = nextDayData.firstResponseDto.fertilizerAmount;
            message = nextDayData.firstResponseDto.message;
        } else if (nextDayData.secondResponseDto != null) {
            roomNo = 2;
            farmStatus = nextDayData.secondResponseDto.farmStatus;
            carbonCaptureStatus = nextDayData.secondResponseDto.carbonCaptureStatus;
            carbonCaptureTryCount = nextDayData.secondResponseDto.carbonCaptureTryCount;
            fertilizerAmountInRoom = nextDayData.secondResponseDto.fertilizerAmount;
            message = nextDayData.secondResponseDto.message;
        } else {
            roomNo = 3;
            fertilizerAmountInRoom = nextDayData.thirdResponseDto.fertilizerAmount;
            message = nextDayData.thirdResponseDto.message;
        }
    }

    public void InitializeAction() {
        inputFertilizerTry = false;
        makeFertilizerTry = false;
        fertilizerUpgradeTry = false;
        purifierTry = false;
        carbonCaptureTry = false;
        taurineFilterTry = false;
        farmTry = false;
        asteroidDestroyTry = false;
        barrierDevTry = false;
        observerTry = false;
    }

    public class DayData
    {
        // JSON 데이터 구조에 맞게 필드 정의
        public FirstRoom firstResponseDto { get; set; }
        public SecondRoom secondResponseDto { get; set; }
        public ThirdRoom thirdResponseDto { get; set; }
        public int fertilizerAmount { get; set; }
        public int eventCode { get; set; }
        public bool fertilizerUpgrade { get; set; }
        public bool barrierUpgrade { get; set; }
        public bool conflictAsteroid { get; set; }
        public string gameRoomId { get; set; }
        public int foodAmount { get; set; }
    }
    public class FirstRoom
    {
        public int fertilizerAmount { get; set; }
        public string message { get; set; }
        public bool purifierStatus { get; set; }
    }

    public class SecondRoom
    {
        public int fertilizerAmount { get; set; }
        public string message { get; set; }
        public bool farmStatus { get; set; }
        public int carbonCaptureStatus { get; set; }
        public int carbonCaptureTryCount { get; set; }
    }

    public class ThirdRoom
    {
        public int fertilizerAmount { get; set; }
        public string message { get; set; }
        // public bool asteroidStatus { get; set; }
    }

}
