using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class NoButton : MonoBehaviour, IPointerClickHandler
{

    // Start is called before the first frame update
    GameObject joystick; 
    GameObject joystickVision;
    GameObject interactiveUIPanel;
    GameObject canvas;
    void Start()
    {   
        canvas = GameObject.Find("Canvas");
        joystick = canvas.transform.Find("Analog").gameObject;
        joystickVision = canvas.transform.Find("AnalogVision").gameObject;

    }
    public void OnPointerClick(PointerEventData eventData){
        Debug.Log("No");
        interactiveUIPanel = GameObject.Find("InteractiveUIPanel");
        interactiveUIPanel.SetActive(false);
        joystick.SetActive(true);
        joystickVision.SetActive(true);
    }
}
