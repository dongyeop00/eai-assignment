package com.gdy.inspien.order.mapper;

import com.gdy.inspien.global.exception.ErrorCode;
import com.gdy.inspien.global.exception.IntegrationException;
import com.gdy.inspien.global.util.IdGenerator;
import com.gdy.inspien.order.dto.OrderDTO;
import com.gdy.inspien.order.dto.OrderHeader;
import com.gdy.inspien.order.dto.OrderItem;
import com.gdy.inspien.order.dto.OrderXmlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final IdGenerator orderIdGenerator;

    @Value("${APPLICANT_KEY}")
    private String applicantKey;

    // XML 문자열을 파싱하여 OrderDto 리스트로 변환
    public List<OrderDTO> parseAndMap(String xmlString) {

        String wrappedXml = "<ROOT>" + xmlString.trim() + "</ROOT>";

        try {
            JAXBContext context = JAXBContext.newInstance(OrderXmlRequest.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(wrappedXml);

            OrderXmlRequest request = (OrderXmlRequest) unmarshaller.unmarshal(reader);
            return mapToOrderDtoList(request);
        } catch (Exception e) {
            throw new IntegrationException(ErrorCode.XML_PARSE_ERROR, e);
        }
    }

    // HEADER와 ITEM을 USER_ID로 조인하여 플랫 구조로 변환
    private List<OrderDTO> mapToOrderDtoList(OrderXmlRequest request) {

        List<OrderDTO> result = new ArrayList<>();

        Map<String, OrderHeader> headerMap = new HashMap<>();
        for (OrderHeader header : request.getHeaders()) {
            headerMap.put(header.getUserId(), header);
        }

        for (OrderItem item : request.getItems()) {

            OrderHeader header = headerMap.get(item.getUserId());

            if (header == null) {
                throw new IntegrationException(ErrorCode.VALIDATION_ERROR);
            }

            OrderDTO dto = OrderDTO.builder()
                    .orderId(orderIdGenerator.generate())
                    .userId(item.getUserId())
                    .itemId(item.getItemId())
                    .applicantKey(applicantKey)
                    .name(header.getName().trim())
                    .address(header.getAddress().trim())
                    .itemName(item.getItemName().trim())
                    .price(item.getPrice().trim())
                    .status(header.getStatus().trim())
                    .build();
            result.add(dto);

        }

        return result;
    }

}
