using System.Collections;
using System.Collections.Generic;
using UnityEditor.Experimental.RestService;
using UnityEngine;

public class VariableInitializer : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {   
        if (VariableManager.Instance.power < VariableManager.Instance.workLimit) {
            VariableManager.Instance.power = 0;
            VariableManager.Instance.workLimit = 0;
        } else {
            VariableManager.Instance.power = 3;
            VariableManager.Instance.workLimit = 1;
        }
        if (VariableManager.Instance.taurineFilterTry) {
            VariableManager.Instance.hasTaurine ++;
        }
        VariableManager.Instance.date += 1;
        VariableManager.Instance.morningEvent = 0;
        int code = VariableManager.Instance.eventCode;
        if ( code%2 == 1 ) {
            VariableManager.Instance.asteroidStatus = true;
        }
        code /= 2;
        if ( code%2 == 1 ) {
            VariableManager.Instance.blackHoleObserved = true;
        }
        code /= 2;
        if ( code%2 == 1 ) {
            VariableManager.Instance.workLimit = 0;
            VariableManager.Instance.morningEvent += 2;
        }
        code /= 2;
        if ( code%2 == 1 ) {
            if (VariableManager.Instance.power > 0) {
                VariableManager.Instance.power --;
            }
            VariableManager.Instance.morningEvent ++;
        }
        code /= 2;
        if ( code%2 == 1 ) {
            VariableManager.Instance.purifierStatus = true;
        }
    }

}
