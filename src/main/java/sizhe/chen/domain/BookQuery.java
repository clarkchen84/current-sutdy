package sizhe.chen.domain;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * BookQuery
 *
 * @Author chensizhe
 * @Date 2022/11/25 7:52 PM
 */
@Data
public class BookQuery {
    private String title;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    @Min(1)
    private int page = 1;

    @Min(0)
    @Max(500)
    private int size = 10;
}
