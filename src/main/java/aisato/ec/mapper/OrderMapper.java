package aisato.ec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import aisato.ec.entity.Order;

/**
 * 注文マーピングインターフェース
 * データベースのordersテーブルへのアクセスを定義
 * @author zhl
 * @version 1.0.0
 * @Mapper インターフェースをMyBatisのマッパーとしてマークする。Springとの連携時に使⽤。
 */
@Mapper
public interface OrderMapper {
    
    /**
     * ユーザーの注文履歴を取得
     * @param userId ユーザーID
     * @return 注文リスト
     */
    List<Order> findByUserId(@Param("userId") Long userId);
    
    /**
     * 注文詳細を取得（注文件含む）
     * @param orderId 注文ID
     * @return 注文エンティティ
     */
    Order findById(@Param("orderId") Long orderId);
    
    /**
     * 注文を登録
     * @param order 注文エンティティ
     * @return 影響を受けた行数
     */
    int insert(Order order);
    
    /**
     * 注文ステータスを更新
     * @param order 注文エンティティ
     * @return 影響を受けた行数
     */
    int updateStatus(Order order);
}