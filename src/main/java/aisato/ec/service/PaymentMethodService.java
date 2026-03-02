package aisato.ec.service;

import java.util.List;

import aisato.ec.entity.PaymentMethod;

/**
 * 支払い方法サービスインターフェース
 * 支払い方法関連のビジネスロジックを定義
 */
public interface PaymentMethodService {
    
    /**
     * 有効な支払い方法をすべて取得
     * @return 有効な支払い方法リスト（表示順にソート）
     */
    List<PaymentMethod> getAllPaymentMethods();
}