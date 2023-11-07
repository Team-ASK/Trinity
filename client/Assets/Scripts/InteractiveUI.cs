using System.Collections;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class InteractiveUI : MonoBehaviour, IPointerClickHandler
{
    // Start is called before the first frame update
    GameObject joystick; 
    GameObject joystickVision;
    void Start()
    {   joystick = GameObject.Find("Analog");
        joystickVision = GameObject.Find("AnalogVision");
        gameObject.SetActive(false);
    }

    // Update is called once per frame
    public void OnPointerClick(PointerEventData eventData){

    gameObject.SetActive(false);
    joystick.SetActive(true);
    joystickVision.SetActive(true);

}
}
