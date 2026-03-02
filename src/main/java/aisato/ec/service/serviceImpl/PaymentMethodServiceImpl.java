package aisato.ec.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aisato.ec.entity.PaymentMethod;
import aisato.ec.mapper.PaymentMethodMapper;
import aisato.ec.service.PaymentMethodService;

/**
 * 支払い方法サービス実装クラス
 * 支払い方法関連のビジネスロジックを実装
 */
@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {
    
	/**
	 * 支払い方法マーピング側呼び出す
	 */
    @Autowired
    private PaymentMethodMapper paymentMethodMapper;
    
    /**
     * 有効な支払い方法をすべて取得
     * @return 有効な支払い方法リスト（表示順にソート）
     */
    @Override
    public List<PaymentMethod> getAllPaymentMethods() {
        // Mapperを呼び出して有効な支払い方法を取得
        // sort_orderでソート済み
        return paymentMethodMapper.findAllActive();
    }
}