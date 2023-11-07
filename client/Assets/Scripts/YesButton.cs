using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class YesButton : MonoBehaviour, IPointerClickHandler
{

    // Start is called before the first frame update
    GameObject joystick; 
    GameObject joystickVision;
    GameObject interactiveUIPanel;
    string target;
    GameObject Target;
    GameObject canvas;
    void Start()
    {   
        canvas = GameObject.Find("Canvas");
        joystick = canvas.transform.Find("Analog").gameObject;
        joystickVision = canvas.transform.Find("AnalogVision").gameObject;

    }
    public void OnPointerClick(PointerEventData eventData){
        Debug.Log("Yes");
        interactiveUIPanel = GameObject.Find("InteractiveUIPanel");
        target = interactiveUIPanel.transform.GetChild(0).transform.GetChild(0).gameObject.GetComponent<RawImage>().texture.name;
        Debug.Log(target);
        Target = GameObject.Find(target);
        Target.gameObject.GetComponent<TouchEvent>().condition = true;
        Debug.Log("Target status: "+ Target.gameObject.GetComponent<TouchEvent>().condition);
        interactiveUIPanel.SetActive(false);
        joystick.SetActive(true);
        joystickVision.SetActive(true);
    }
}
