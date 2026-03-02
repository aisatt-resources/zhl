package aisato.ec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import aisato.ec.entity.UserPaymentSetting;

/**
 * ユーザー支払い設定マーケティングインターフェース
 * データベースのuser_payment_settingsテーブルへのアクセスを定義
 */
@Mapper
public interface UserPaymentSettingMapper {
    
    /**
     * ユーザーのデフォルト支払い方法IDを取得
     * @param userId ユーザーID
     * @return デフォルト支払い方法ID（存在しない場合はnull）
     */
    Long findDefaultMethodIdByUserId(@Param("userId") Long userId);
    
    /**
     * ユーザーの支払い設定を取得
     * @param userId ユーザーID
     * @return 支払い設定エンティティ（存在しない場合はnull）
     */
    UserPaymentSetting findByUserId(@Param("userId") Long userId);
    
    /**
     * 支払い設定を挿入
     * @param setting 支払い設定エンティティ
     * @return 影響を受けた行数
     */
    int insert(UserPaymentSetting setting);
    
    /**
     * 支払い設定を更新
     * @param setting 支払い設定エンティティ
     * @return 影響を受けた行数
     */
    int update(UserPaymentSetting setting);
    
    /**
     * 支払い設定を削除
     * @param userId ユーザーID
     * @return 影響を受けた行数
     */
    int deleteByUserId(@Param("userId") Long userId);
}