package com.gdy.inspien.order.mapper;

import com.gdy.inspien.global.util.IdGenerator;
import com.gdy.inspien.order.dto.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private OrderMapper orderMapper;
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        idGenerator = new IdGenerator();
        idGenerator.reset();
        orderMapper = new OrderMapper(idGenerator);
        // @Value 필드 주입
        ReflectionTestUtils.setField(orderMapper, "applicantKey", "TEST000001");
    }

    @Test
    @DisplayName("XML 파싱 및 매핑 성공")
    void parseAndMap_success() {
        // given
        String xml = """
            <HEADER>
                <USER_ID>USER1</USER_ID>
                <NAME>홍길동</NAME>
                <ADDRESS>서울특별시 금천구</ADDRESS>
                <STATUS>N</STATUS>
            </HEADER>
            <ITEM>
                <USER_ID>USER1</USER_ID>
                <ITEM_ID>ITEM1</ITEM_ID>
                <ITEM_NAME>청바지</ITEM_NAME>
                <PRICE>21000</PRICE>
            </ITEM>
            """;

        // when
        List<OrderDTO> result = orderMapper.parseAndMap(xml);

        for (OrderDTO orderDTO : result){
            System.out.println("결과 : " + orderDTO.toString());
        }

        // then
        assertThat(result).hasSize(1);

        OrderDTO order = result.get(0);
        assertThat(order.getOrderId()).isEqualTo("A000");
        assertThat(order.getUserId()).isEqualTo("USER1");
        assertThat(order.getItemId()).isEqualTo("ITEM1");
        assertThat(order.getName()).isEqualTo("홍길동");
        assertThat(order.getAddress()).isEqualTo("서울특별시 금천구");
        assertThat(order.getItemName()).isEqualTo("청바지");
        assertThat(order.getPrice()).isEqualTo("21000");
        assertThat(order.getStatus()).isEqualTo("N");
        assertThat(order.getApplicantKey()).isEqualTo("TEST000001");
    }

    @Test
    @DisplayName("여러 주문 XML 파싱 성공")
    void parseAndMap_multipleOrders() {
        // given
        String xml = """
            <HEADER>
                <USER_ID>USER1</USER_ID>
                <NAME>홍길동</NAME>
                <ADDRESS>서울특별시 금천구</ADDRESS>
                <STATUS>N</STATUS>
            </HEADER>
            <HEADER>
                <USER_ID>USER2</USER_ID>
                <NAME>유관순</NAME>
                <ADDRESS>서울특별시 구로구</ADDRESS>
                <STATUS>N</STATUS>
            </HEADER>
            <ITEM>
                <USER_ID>USER1</USER_ID>
                <ITEM_ID>ITEM1</ITEM_ID>
                <ITEM_NAME>청바지</ITEM_NAME>
                <PRICE>21000</PRICE>
            </ITEM>
            <ITEM>
                <USER_ID>USER2</USER_ID>
                <ITEM_ID>ITEM2</ITEM_ID>
                <ITEM_NAME>티셔츠</ITEM_NAME>
                <PRICE>15800</PRICE>
            </ITEM>
            """;

        // when
        List<OrderDTO> result = orderMapper.parseAndMap(xml);

        for (OrderDTO orderDTO : result){
            System.out.println("결과 : " + orderDTO.toString());
        }

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("홍길동");
        assertThat(result.get(1).getName()).isEqualTo("유관순");
    }
}