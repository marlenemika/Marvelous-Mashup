package de.uulm.sopra.team08.req;

import de.uulm.sopra.team08.EntityID;
import de.uulm.sopra.team08.util.Role;
import de.uulm.sopra.team08.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * tests the hashCode-equals contract
 */
public class RequestsEqualsContractTest {

    @Test
    void generalRequestTest() {
        Assertions.assertFalse(new MMIngameRequest(MMRequest.requestType.HELLO_SERVER).getRequestType().isIngame());
        Assertions.assertTrue(new MMLoginRequest(MMRequest.requestType.REQ).getRequestType().isIngame());

        MMIngameRequest ingameRequest = new MMIngameRequest(MMRequest.requestType.REQ);
        MMIngameRequest ingameRequest2 = new MMIngameRequest(MMRequest.requestType.REQ);
        MMIngameRequest ingameRequest3 = new MMIngameRequest(MMRequest.requestType.USE_INFINITY_STONE);
        MMLoginRequest loginRequest = new MMLoginRequest(MMRequest.requestType.ERROR);
        MMLoginRequest loginRequest2 = new MMLoginRequest(MMRequest.requestType.ERROR);
        MMLoginRequest loginRequest3 = new MMLoginRequest(MMRequest.requestType.HELLO_SERVER);

        Assertions.assertEquals("{\"requestType\":\"Req\"}", ingameRequest.toJsonRequest());
        Assertions.assertEquals("MMIngameRequest", ingameRequest.toString());
        Assertions.assertEquals(MMRequest.requestType.REQ, ingameRequest.getRequestType());

        Assertions.assertEquals(ingameRequest, ingameRequest);
        Assertions.assertNotEquals(null, ingameRequest);
        Assertions.assertNotEquals(ingameRequest, loginRequest);
        Assertions.assertEquals(ingameRequest, ingameRequest2);
        Assertions.assertNotEquals(ingameRequest, ingameRequest3);

        Assertions.assertNotEquals(ingameRequest.hashCode(), ingameRequest3.hashCode());

        Assertions.assertEquals("{\"messageType\":\"ERROR\"}", loginRequest.toJsonRequest());
        Assertions.assertEquals("MMLoginRequest", loginRequest.toString());
        Assertions.assertEquals(MMRequest.requestType.ERROR, loginRequest.getRequestType());

        Assertions.assertEquals(loginRequest, loginRequest);
        Assertions.assertNotEquals(null, loginRequest);
        Assertions.assertNotEquals(ingameRequest, loginRequest);
        Assertions.assertEquals(loginRequest, loginRequest2);
        Assertions.assertNotEquals(loginRequest, loginRequest3);

        Assertions.assertNotEquals(loginRequest.hashCode(), loginRequest3.hashCode());
    }

    @Test
    void useInifinityStoneReq() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 7);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 6);
        final UseInfinityStoneRequest e1 = new UseInfinityStoneRequest(originEntity, targetEntity, originField, targetField, stoneType);
        final UseInfinityStoneRequest e2 = new UseInfinityStoneRequest(originEntity, targetEntity, originField, targetField, stoneType);
        final UseInfinityStoneRequest e3 = new UseInfinityStoneRequest(new Tuple<>(EntityID.P1, 1), targetEntity, originField, targetField, stoneType);
        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void exchangeInfinityStoneReq() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 7);
        Tuple<EntityID, Integer> stoneType = new Tuple<>(EntityID.INFINITYSTONES, 6);
        final ExchangeInfinityStoneRequest e1 = new ExchangeInfinityStoneRequest(originEntity, targetEntity, originField, targetField, stoneType);
        final ExchangeInfinityStoneRequest e2 = new ExchangeInfinityStoneRequest(originEntity, targetEntity, originField, targetField, stoneType);
        final ExchangeInfinityStoneRequest e3 = new ExchangeInfinityStoneRequest(new Tuple<>(EntityID.P1, 1), targetEntity, originField, targetField, stoneType);
        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void meleeAttackReq() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final var e1 = new MeleeAttackRequest(originEntity, targetEntity, originField, targetField, 1);
        final var e2 = new MeleeAttackRequest(originEntity, targetEntity, originField, targetField, 1);
        final var e3 = new MeleeAttackRequest(new Tuple<>(EntityID.P1, 5), targetEntity, originField, targetField, 1);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void rangedAttackReq() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<EntityID, Integer> targetEntity = new Tuple<>(EntityID.P2, 4);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final var e1 = new RangedAttackRequest(originEntity, targetEntity, originField, targetField, 1);
        final var e2 = new RangedAttackRequest(originEntity, targetEntity, originField, targetField, 1);
        final var e3 = new RangedAttackRequest(new Tuple<>(EntityID.P1, 5), targetEntity, originField, targetField, 1);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void moveReq() {
        Tuple<EntityID, Integer> originEntity = new Tuple<>(EntityID.P1, 3);
        Tuple<Integer, Integer> originField = new Tuple<>(3, 7);
        Tuple<Integer, Integer> targetField = new Tuple<>(3, 6);
        final var e1 = new MoveRequest(originEntity, originField, targetField);
        final var e2 = new MoveRequest(originEntity, originField, targetField);
        final var e3 = new MoveRequest(originEntity, new Tuple<>(2, 7), targetField);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }


    @Test
    void error() {
        final ErrorRequest e1 = new ErrorRequest("e", 1);
        final ErrorRequest e2 = new ErrorRequest("e", 1);
        final ErrorRequest e3 = new ErrorRequest("e", 2);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }


    @Test
    void helloServer() {
        final HelloServer e1 = new HelloServer("e1", "d");
        final HelloServer e2 = new HelloServer("e1", "d");
        final HelloServer e3 = new HelloServer("e3", "d");

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void playerReady() {
        final PlayerReady e1 = new PlayerReady(true, Role.PLAYER);
        final PlayerReady e2 = new PlayerReady(true, Role.PLAYER);
        final PlayerReady e3 = new PlayerReady(false, Role.PLAYER);
        final PlayerReady e4 = new PlayerReady(true, Role.KI);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, e4);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void reconnect() {
        final Reconnect e1 = new Reconnect(true);
        final Reconnect e2 = new Reconnect(true);
        final Reconnect e3 = new Reconnect(false);

        Assertions.assertEquals(e1, e2);
        Assertions.assertEquals(e1, e1);
        Assertions.assertNotEquals(e1, e3);
        Assertions.assertNotEquals(e1, null);
        Assertions.assertEquals(e1.hashCode(), e2.hashCode());
        Assertions.assertNotEquals(e1.hashCode(), e3.hashCode());
    }

}
