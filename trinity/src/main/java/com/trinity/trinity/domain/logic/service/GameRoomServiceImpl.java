package com.trinity.trinity.domain.logic.service;

import com.trinity.trinity.domain.logic.dto.*;
import com.trinity.trinity.global.redis.service.GameRoomRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomServiceImpl implements GameRoomService {

    private final GameRoomRedisService gameRoomRedisService;

    @Override
    public String gameLogic(GameRoom gameRoom) {
        FirstRoom firstRoom = gameRoom.getFirstRoom();
        SecondRoom secondRoom = gameRoom.getSecondRoom();
        ThirdRoom thirdRoom = gameRoom.getThirdRoom();

        gameRoom.modifyGameRound(gameRoom.getRoundNo() + 1);
        gameRoom.modifyFoodAmount(gameRoom.getFoodAmount() - 1);

        if (gameRoom.isCarbonCaptureNotice()) gameRoom.modifyCarbonCaptureNotice(false);

        // 식량 점검
        if (!checkFoodAmount(gameRoom, thirdRoom)) {
            log.info("식량 없어서 뒤짐");
            return "starve";
        }

        // 정수 시스템 점검
        if (!checkPurifierStatus(firstRoom, secondRoom)) {
            log.info("정수 시스템 고장나서 뒤짐");
            return "contaminated";
        }

        // 이산화탄소 포집기 점검
        if (!checkCarbonCaptureStatus(secondRoom)) {
            log.info("이산화탄소 포집기 고장나서 뒤짐");
            return "suffocation";
        }

        // 비료 생성기 업그레이드
        checkFertilizerUpgradeStatus(firstRoom, secondRoom, thirdRoom);

        // 타우린
        checkTaurineFilterStatus(firstRoom, secondRoom);

        // 블랙홀
        if (thirdRoom.isBlackholeStatus()) {
            if (gameRoom.getRoundNo() + 2 <= 12) gameRoom.getBlackholeStatus()[gameRoom.getRoundNo() + 2] = true;
        }

        gameRoomRedisService.saveGameRoomToTemp(gameRoom);

        return "alive";
    }

    private void checkTaurineFilterStatus(FirstRoom firstRoom, SecondRoom secondRoom) {
        if (secondRoom.isTaurineFilterTry()) {
            secondRoom.modifyTaurineFilterTry(false);
            secondRoom.modifyTaurineFilterStatus(false);
            firstRoom.modifyPurifierStatus(1);
        }
    }

    private void checkFertilizerUpgradeStatus(FirstRoom firstRoom, SecondRoom secondRoom, ThirdRoom thirdRoom) {
        if (firstRoom.isFertilizerUpgradeTry()) {
            firstRoom.modifyFertilizerUpgradeTry(false);
            firstRoom.modifyFertilizerUpgradeStatus(firstRoom.getFertilizerUpgradeStatus() + 1);
        }

        if (firstRoom.getFertilizerUpgradeStatus() == 3) makeFertilizer(firstRoom, secondRoom, thirdRoom, 3);
        else makeFertilizer(firstRoom, secondRoom, thirdRoom, 2);
    }

    private boolean checkFoodAmount(GameRoom gameRoom, ThirdRoom thirdRoom) {
        // 자동 보호막
        checkBarrier(thirdRoom);
        // 소행성
        checkAsteroidStatus(gameRoom);

        if (gameRoom.getFoodAmount() == 0) {
            return false;
        }

        return true;
    }

    private void checkAsteroidStatus(GameRoom gameRoom) {
        FirstRoom firstRoom = gameRoom.getFirstRoom();
        SecondRoom secondRoom = gameRoom.getSecondRoom();
        ThirdRoom thirdRoom = gameRoom.getThirdRoom();

        if (thirdRoom.isAsteroidStatus()) {
            if (thirdRoom.isAsteroidDestroyTry()) {
                thirdRoom.modifyAsteroidDestroyTry(false);

                checkFarm(gameRoom, firstRoom, secondRoom, thirdRoom);
            } else {
                if (thirdRoom.getBarrierStatus() < 2) {
                    if (secondRoom.isFarmTry()) checkFarm(gameRoom, firstRoom, secondRoom, thirdRoom);
                    secondRoom.modifyFarmStatus(false);
                    gameRoom.modifyFertilizerAmount(0);
                } else {
                    checkFarm(gameRoom, firstRoom, secondRoom, thirdRoom);
                }
            }

            thirdRoom.modifyAsteroidStatus(false);
        } else checkFarm(gameRoom, firstRoom, secondRoom, thirdRoom);
    }

    private void checkBarrier(ThirdRoom thirdRoom) {
        if (thirdRoom.isBarrierDevTry()) {
            thirdRoom.modifyBarrierDevTry(false);
            if (thirdRoom.getUserId().equals(thirdRoom.getDeveloper())) {
                thirdRoom.modifyBarrierStatus(thirdRoom.getBarrierStatus() + 1);
            } else {
                thirdRoom.modifyBarrierStatus(1);
                thirdRoom.modifyDeveloper(thirdRoom.getUserId());
            }
        }
    }

    private boolean checkCarbonCaptureStatus(SecondRoom secondRoom) {
        if (secondRoom.getCarbonCaptureStatus() > 0) {
            secondRoom.modifyCarbonCaptureStatus(secondRoom.getCarbonCaptureStatus() + 1);
            if (secondRoom.isCarbonCaptureTry()) {
                secondRoom.modifyCarbonCaptureTry(false);
                secondRoom.modifyCarbonCaptureTryCount(secondRoom.getCarbonCaptureTryCount() + 1);
            }
            if (secondRoom.getCarbonCaptureTryCount() == 2) {
                secondRoom.modifyCarbonCaptureStatus(0);
                secondRoom.modifyCarbonCaptureTryCount(0);
            }
            if (secondRoom.getCarbonCaptureStatus() == 4) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPurifierStatus(FirstRoom firstRoom, SecondRoom secondRoom) {
        if (firstRoom.getPurifierStatus() > 0) {
            firstRoom.modifyPurifierStatus(firstRoom.getPurifierStatus() + 1);
            if (firstRoom.isPurifierTry()) {
                firstRoom.modifyPurifierTry(false);
                firstRoom.modifyPurifierStatus(0);
                secondRoom.modifyTaurineFilterStatus(true);
            }

            if (firstRoom.getPurifierStatus() == 3) {
                return false;
            }

        } else {
            if (firstRoom.isPurifierTry()) {
                firstRoom.modifyPurifierTry(false);
                firstRoom.modifyPurifierStatus(1);
                secondRoom.modifyTaurineFilterStatus(false);
            }
        }

        return true;
    }

    @Override
    public void morningGameLogic(GameRoom gameRoom) {
        SecondRoom secondRoom = gameRoom.getSecondRoom();
        ThirdRoom thirdRoom = gameRoom.getThirdRoom();

        // 블랙홀 영향권인지 판단
        if (gameRoom.getBlackholeStatus()[gameRoom.getRoundNo()]) {
            movePlayer(0, gameRoom);
            thirdRoom.modifyBlackholeStatus(false);
        } else {
            movePlayer(1, gameRoom);
        }

        gameRoom = checkEvent(gameRoom);

        // 이산화탄소 고장 일수 2일차인지 판단
        if (secondRoom.getCarbonCaptureStatus() == 2) gameRoom.modifyCarbonCaptureNotice(true);

        // 로직 끝
        gameRoomRedisService.saveGameRoomToTemp(gameRoom);
        gameRoomRedisService.saveGameRoom(gameRoom);
    }

    @Override
    public GameRoom checkEvent(GameRoom gameRoom) {
        Events events = gameRoom.getEvents();

        // 라운드 수와 이벤트 종류에 따라 계산한 event 종류 저장
        int eventIdx = 0;

        int[][] eventArrays = {
                events.getAsteroid(),
                events.getBlackhole(),
                events.getBirthday(),
                events.getSickness(),
                events.getPurifier(),
                events.getCarbonCapture()
        };

        int[] eventWeights = {1, 2, 4, 8, 16, 32};

        for (int i = 0; i < 6; i++) {
            int[] arr = eventArrays[i];
            int value = eventWeights[i];

            for (int j = 0; j < arr.length; j++) {
                if (arr[j] == gameRoom.getRoundNo()) {
                    eventIdx += value;
                    // 선택된 이벤트에 따라 데이터 변경
                    validateEvent(i, gameRoom);
                    break;
                }
            }
        }

        // 식사는 10일에 고정
        if (gameRoom.getRoundNo() == 10) {
            eventIdx += 64;
            validateEvent(6, gameRoom);
        }

        gameRoom.modifyEvent(eventIdx);

        return gameRoom;
    }

    @Override
    public boolean checkEndGame(GameRoom gameRoom) {
        if (gameRoom.getRoundNo() == 13) {
            gameRoomRedisService.deleteGameRoom(gameRoom.getGameRoomId());
            return true;
        }
        return false;
    }

    @Override
    public void endGame(String gameRoomId) {
        gameRoomRedisService.deleteGameRoom(gameRoomId);
    }

    private void movePlayer(int direction, GameRoom gameRoom) {
        FirstRoom firstRoom = gameRoom.getFirstRoom();
        SecondRoom secondRoom = gameRoom.getSecondRoom();
        ThirdRoom thirdRoom = gameRoom.getThirdRoom();

        // 정방향
        if (direction == 1) {
            String temp = thirdRoom.getUserId();
            thirdRoom.modifyUserId(secondRoom.getUserId());
            secondRoom.modifyUserId(firstRoom.getUserId());
            firstRoom.modifyUserId(temp);

            return;
        }

        // 역방향
        String temp = thirdRoom.getUserId();
        thirdRoom.modifyUserId(firstRoom.getUserId());
        firstRoom.modifyUserId(secondRoom.getUserId());
        secondRoom.modifyUserId(temp);
    }

    private void validateEvent(int eventIdx, GameRoom gameRoom) {
        FirstRoom firstRoom = gameRoom.getFirstRoom();
        SecondRoom secondRoom = gameRoom.getSecondRoom();
        ThirdRoom thirdRoom = gameRoom.getThirdRoom();

        switch (eventIdx) {
            case 0:
                thirdRoom.modifyAsteroidStatus(true);
                break;
            case 1:
                thirdRoom.modifyBlackholeStatus(true);
                break;
            case 2:
                gameRoom.modifyBirthday(true);
                break;
            case 3:
                gameRoom.modifyPlayerStatus(true);
                break;
            case 4:
                firstRoom.modifyPurifierStatus(1);
                break;
            case 5:
                if (secondRoom.getCarbonCaptureStatus() == 0) secondRoom.modifyCarbonCaptureStatus(1);
                break;
            case 6:
                gameRoom.modifyFoodAmount(gameRoom.getFoodAmount() + 1);
                break;
        }
    }

    private void makeFertilizer(FirstRoom firstRoom, SecondRoom secondRoom, ThirdRoom thirdRoom, int fertilizer) {
        if (firstRoom.isMakeFertilizerTry()) {
            firstRoom.modifyMakeFertilizerTry(false);
            firstRoom.modifyFertilizerAmount(firstRoom.getFertilizerAmount() + fertilizer);
        }
        if (secondRoom.isMakeFertilizerTry()) {
            secondRoom.modifyMakeFertilizerTry(false);
            secondRoom.modifyFertilizerAmount(secondRoom.getFertilizerAmount() + fertilizer);
        }
        if (thirdRoom.isMakeFertilizerTry()) {
            thirdRoom.modifyMakeFertilizerTry(false);
            thirdRoom.modifyFertilizerAmount(thirdRoom.getFertilizerAmount() + fertilizer);
        }
    }

    private void checkFarm(GameRoom gameRoom, FirstRoom firstRoom, SecondRoom secondRoom, ThirdRoom thirdRoom) {
        if (!secondRoom.isFarmStatus()) {
            if (secondRoom.isFarmTry()) {
                secondRoom.modifyFarmTry(false);
                secondRoom.modifyFarmStatus(true);
                // 식량 생산
                makeFood(gameRoom, firstRoom, secondRoom, thirdRoom);
            }
        } else {
            // 식량 생산
            makeFood(gameRoom, firstRoom, secondRoom, thirdRoom);
        }
    }

    private void makeFood(GameRoom gameRoom, FirstRoom firstRoom, SecondRoom secondRoom, ThirdRoom thirdRoom) {
        int count = 0;
        if (firstRoom.isInputFertilizerTry()) {
            firstRoom.modifyInputFertilizerTry(false);
            firstRoom.modifyFertilizerAmount(firstRoom.getFertilizerAmount() - 1);
            count++;
        }
        if (secondRoom.isInputFertilizerTry()) {
            secondRoom.modifyInputFertilizerTry(false);
            secondRoom.modifyFertilizerAmount(secondRoom.getFertilizerAmount() - 1);
            count++;
        }
        if (thirdRoom.isInputFertilizerTry()) {
            thirdRoom.modifyInputFertilizerTry(false);
            thirdRoom.modifyFertilizerAmount(thirdRoom.getFertilizerAmount() - 1);
            count++;
        }

        int fertilizer = gameRoom.getFertilizerAmount() + count;
        if (fertilizer >= 4) {
            gameRoom.modifyFertilizerAmount(0);
            gameRoom.modifyFoodAmount(gameRoom.getFoodAmount() + 2);
        } else {
            gameRoom.modifyFertilizerAmount(fertilizer);
        }
    }
}
