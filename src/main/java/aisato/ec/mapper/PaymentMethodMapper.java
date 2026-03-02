package aisato.ec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import aisato.ec.entity.PaymentMethod;

/**
 * 支払い方法マーピングインターフェース
 * データベースのpayment_methodsテーブルへのアクセスを定義
 */
@Mapper
public interface PaymentMethodMapper {
    
    /**
     * 有効な支払い方法をすべて取得（表示順にソート）
     * @return 有効な支払い方法リスト
     */
    List<PaymentMethod> findAllActive();
}