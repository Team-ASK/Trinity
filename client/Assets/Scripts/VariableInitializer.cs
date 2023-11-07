using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class VariableInitializer : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        PlayerPrefs.SetInt("Power", 3);
        PlayerPrefs.SetInt("HasTaurine", 0);
        PlayerPrefs.SetInt("FatigueLevel", 0);
        PlayerPrefs.SetInt("WorkLimit", 1);
        PlayerPrefs.SetString("Room1", "BiRyoRoom");
        PlayerPrefs.SetString("Room2", "controlRoom");
        PlayerPrefs.SetString("Room3", "medicineroom");
        PlayerPrefs.SetInt("RoomNo", 0);
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
