import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import ModelViewer from '../components/3dmodelviewer'
import AnimatedNeonButton from '../components/neonbtn';


const MainHome: React.FC = () => {
    const navigate = useNavigate();
    const policy = () => {
        navigate("/privatepolicy");
    };
    const intro = () => {
      navigate("/intro");
  };
  
    
    return (
      <div>
      {/* <div style={{ position: 'relative', overflow: 'hidden' }}> */}
         <div>
          <ModelViewer modelPath="/assets/spaceship_colored_joint.fbx" fileType="fbx"/>
        </div>
        <div>
        {/* <div style={{ position: 'absolute', top: '20px', left: '37%', zIndex: 1 }}> */}
          <div style={{display:'flex', flexDirection: 'column', alignItems:'center', justifyContent:'center'}}>
          <h1 style={{color:'gold'}}>Trinity</h1>
            <Link to="/intro">
              <img src="assets/Trinity_logo.png" alt="Logo" style={{ width:'300px'}}/>
            </Link>
            <h2 style={{color:'orange'}}>made by unity</h2>
              <h1 onClick={intro} style={{cursor: 'pointer', color:'red'}}>Trinity Story</h1>
            <p onClick={policy} style={{fontSize:'55px', cursor: 'pointer', marginTop:'50px', color:'red'}}>개인정보 처리 방침</p>
          </div>
        </div>
        <div style={{ position: 'absolute', top: '20px', left: '20px', zIndex: 2 }}>
          <AnimatedNeonButton href='https://drive.google.com/file/d/1sq3fgaEX76k5xtSJYTG9-ybGeaWIGKQ3/view'>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          Trinity Download
        </AnimatedNeonButton>
        </div>
      </div>
    );
}
export default MainHome;