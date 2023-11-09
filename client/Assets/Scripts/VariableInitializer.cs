using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class VariableInitializer : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        Timeout(6.0f);
    }

    IEnumerator Timeout(float time){
        yield return new WaitForSeconds(time);
        VariableControl();
    }

    public void VariableControl()
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
        if (VariableManager.Instance.usedTaurine) {
            VariableManager.Instance.workLimit = 0;
            VariableManager.Instance.usedTaurine = false;
        }
        VariableManager.Instance.date ++;
        VariableManager.Instance.morningEvent = 0;
        int code = VariableManager.Instance.eventCode;
        if ( code%2 == 1 ) {
            VariableManager.Instance.asteroidStatus = true;
        }  // 이벤트 코드 1 : 소행성 출현
        code /= 2;
        if ( code%2 == 1 ) {
            VariableManager.Instance.blackHoleObserved = true;
        } else {
            VariableManager.Instance.blackHoleObserved = false;
        }  // 이벤트 코드 2 : 블랙홀 출현
        code /= 2;
        if ( code%2 == 1 ) {
            VariableManager.Instance.workLimit = 0;
            VariableManager.Instance.morningEvent += 2;
        }  // 이벤트 코드 4 : 트리니티 호의 생일 (과로 면역)
        code /= 2;
        if ( code%2 == 1 ) {
            if (VariableManager.Instance.power > 0) {
                VariableManager.Instance.power --;
            }
            VariableManager.Instance.morningEvent ++;
        }  // 이벤트 코드 8 : 우주 멀미 (가능한 행동 카운트 -1)
        code /= 2;
        if ( code%2 == 1 ) {
            VariableManager.Instance.purifierStatus = true;
        }  // 이벤트 코드 16 : 정수 시스템 고장
    }
}
