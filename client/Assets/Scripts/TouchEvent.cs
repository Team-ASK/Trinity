using Unity.VisualScripting;
using UnityEngine;
using UnityEngine.EventSystems;

using UnityEngine.UI;
public class TouchEvent : MonoBehaviour, IPointerClickHandler
{
    string objectName;
    public bool condition = false;

    GameObject canvas;
    GameObject joystick;
    GameObject joystickVision;
    void Start()
    {
        condition = false;
        objectName = this.gameObject.name;
        Debug.Log("hi "+objectName);
        joystick = GameObject.Find("Analog");
        joystickVision = GameObject.Find("AnalogVision");
        canvas = GameObject.Find("Canvas");
    }
    public void OnPointerClick(PointerEventData eventData){
        Debug.Log(condition);
        PlayerPrefs.SetInt("Power", PlayerPrefs.GetInt("Power")-1);
        RenderTexture texture; 

        string PATH = "InteractiveUI/RenderTexture/Render" + objectName;    //이미지 위치를 저장하는 변수
        texture = Resources.Load(PATH, typeof(RenderTexture)) as RenderTexture;  //이미지 로드
        Debug.Log(texture);
        canvas.transform.Find("InteractiveUIPanel").transform.Find("InteractiveCameraImage").GetComponent<RawImage>().texture = texture;

        Texture2D templateImage;
        string PATH_template = "InteractiveUI/"+objectName;
        Debug.Log(PATH_template);
        templateImage = Resources.Load(PATH_template, typeof(Texture2D)) as Texture2D;
        Debug.Log(templateImage);
        canvas.transform.Find("InteractiveUIPanel").transform.Find("InteractiveCameraImage").transform.Find("InteractiveUI").GetComponent<RawImage>().texture = templateImage;




        canvas.transform.Find("InteractiveUIPanel").gameObject.SetActive(true);
        joystick.SetActive(false);
        joystickVision.SetActive(false);
    }
}
    // Update is called once per frame