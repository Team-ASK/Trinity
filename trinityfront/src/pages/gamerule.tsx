import React from 'react';
import { useNavigate } from 'react-router-dom';
import AnimatedNeonButton from '../components/neonbtn';

const GameRule: React.FC = () => {
  const navigate = useNavigate();
  const biryo = () => {
      window.scrollTo(0, 0);
      navigate("/biryoroom");
  };
  const medi = () => {
    window.scrollTo(0, 0);
    navigate("/mediroom");
  }
  const ctrl = () => {
    window.scrollTo(0, 0);
    navigate("/ctrlroom");
  }
    
    return (
    <div style={{display:'flex', flexDirection: 'column', alignItems:'center', justifyContent:'center'}}>
        <h1 style={{fontSize:'100px'}}>Trinity Rule</h1>
        <p style={{fontSize:'55px'}}>총 12Round!</p>
        <p style={{fontSize:'55px'}}>주어진 시간은 45초!</p>
        <p style={{fontSize:'55px'}}>하루에 할 수 있는일은 최대 3번 </p>
        <p style={{fontSize:'55px'}}>그러나 3번 일을하면 다음날은 과로로 일을 할 수 없게된다.</p>
        <p style={{fontSize:'55px'}}>팀원들과 소통을 할 수 있는 유일한 방법은...!</p>
        <img src="assets/Trinity_memo_1.png" alt="logo" />
        <p style={{fontSize:'55px'}}>메모에 한 글자를 적는 것..!</p>
        <p style={{fontSize:'55px'}}>식량 생산에 필요한 비료는 총 4개!</p>
        <p style={{fontSize:'55px'}}>누가 비료를 넣었다고 생각하고</p>
        <p style={{fontSize:'55px'}}>비료를 투입하지 않으면</p>
        <p style={{fontSize:'55px'}}>식량이 없어서 굶어죽게 된다..!</p>
        <p style={{fontSize:'55px'}}>설비들이 고장났는데</p>
        <p style={{fontSize:'55px'}}>제한시간 내 수리하지 못한다면..!</p>
        <p style={{fontSize:'55px'}}>생존하지 못한다!</p>
        <p style={{fontSize:'55px'}}>이제 각 방에서 할 수 있는 일을 확인하여 협동하라!</p>
        <div style={{display:'flex', flexDirection: 'row', alignItems:'center', justifyContent:'center'}}>
            <div style={{display:'flex', flexDirection: 'column', alignItems:'center', justifyContent:'center', cursor: 'pointer'}} onClick={biryo}>
              <img src="assets/Biryo.png" alt="" style={{ width: '300px', height:'300px'}} />
              <p style={{fontSize:'25px'}}>식량생산실</p>
            </div>
            <div style={{display:'flex', flexDirection: 'column', alignItems:'center', justifyContent:'center', padding:'50px', cursor: 'pointer'}} onClick={medi}>
              <img src="assets/medi.png" alt="" style={{ width: '300px', height:'300px'}} />
              <p style={{fontSize:'25px'}}>약제실</p>
            </div>
            <div style={{display:'flex', flexDirection: 'column', alignItems:'center', justifyContent:'center', cursor: 'pointer'}} onClick={ctrl}>
              <img src="assets/ctrl.png" alt="" style={{ width: '300px', height:'300px'}} />
              <p style={{fontSize:'25px'}}>관제실</p>
            </div>
        </div>
        <div style={{display:'flex', flexDirection: 'column', alignItems:'center', justifyContent:'center'}}>
          <AnimatedNeonButton href='/'>
              <span></span>
              <span></span>
              <span></span>
              <span></span>
              Home
          </AnimatedNeonButton>
        </div>
        <br />
    </div>
    );
}
export default GameRule;
