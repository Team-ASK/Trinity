import React, { useRef, useEffect } from 'react';
import * as THREE from 'three';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
// import styled from 'styled-components';

interface ModelViewerProps {
  modelPath: string;
  fileType: 'fbx' | 'gltf';
}

const ModelViewer: React.FC<ModelViewerProps> = ({ modelPath, fileType }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const modelRef = useRef<THREE.Object3D | null>(null);

  useEffect(() => {
    if (!containerRef.current) return;

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
    const renderer = new THREE.WebGLRenderer({ alpha: true });

    // 
    renderer.setSize(window.innerWidth, window.innerHeight);
    containerRef.current.appendChild(renderer.domElement);

    // 카메라 위치 조절
    camera.position.z = 5;
    // 더 나은 가시성을 위해 FOV 조절
    const aspect = window.innerWidth / window.innerHeight;
    const verticalFOV = 120; // 여러분의 요구에 맞게 이 값을 조절하세요.

    camera.fov = (verticalFOV / aspect) * (180 / Math.PI);
    camera.updateProjectionMatrix();

    let model: THREE.Group | THREE.Object3D | undefined;

    // Load model based on file type
    if (fileType === 'fbx') {
      const loader = new FBXLoader();
      loader.load(modelPath, (fbx) => {
        model = fbx;
        scene.add(model);

        // 모델의 크기와 위치 조절 (예시)
        model.scale.set(0.0003, 0.0003, 0.0003);
        model.position.set(0, 0, 0);
      });
    } else if (fileType === 'gltf') {
      const loader = new GLTFLoader();
      loader.load(modelPath, (gltf) => {
        model = gltf.scene;
        scene.add(model);

        // 모델의 크기와 위치 조절 (예시)
        model.scale.set(0.00085, 0.00085, 0.00085);
        model.position.set(0, 0, 0);
      });
    }

    const animate = () => {
      requestAnimationFrame(animate);

      // 모델이 로드된 후에만 회전 조절
      if (model) {
        model.rotation.y += 0.001;
        model.rotation.z += 0.001;
        model.rotation.x += 0.001;
      }

      // 모델이 스스로 이동하는 로직
      if (modelRef.current) {
        // 여기에서 모델의 위치를 조절합니다.
        modelRef.current.position.x += 10;
        modelRef.current.rotation.y += 10;
      }

      renderer.render(scene, camera);
    };

    animate();

    // Cleanup on unmount
    const currentContainerRef = containerRef.current; // Save a reference
    return () => {
      currentContainerRef?.removeChild(renderer.domElement);
      renderer.dispose();
    };
  }, [modelPath, fileType]);

  return <div ref={containerRef} />;
};

export default ModelViewer;