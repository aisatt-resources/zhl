package aisato.ec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import aisato.ec.entity.UserAddress;

/**
 * ユーザー住所マーケティングインターフェース
 * データベースのuser_addressesテーブルへのアクセスを定義
 */
@Mapper
public interface UserAddressMapper {
    
    /**
     * ユーザーの住所を取得
     * @param userId ユーザーID
     * @return 住所リスト
     */
    List<UserAddress> findByUserId(@Param("userId") Long userId);
    
    /**
     * 住所を取得
     * @param addressId 住所ID
     * @return 住所エンティティ
     */
    UserAddress findById(@Param("addressId") Long addressId);
    
    /**
     * 住所を追加
     * @param address 住所エンティティ
     * @return 影響を受けた行数
     */
    int insert(UserAddress address);
    
    /**
     * 住所を更新
     * @param address 住所エンティティ
     * @return 影響を受けた行数
     */
    int update(UserAddress address);
    
    /**
     * デフォルトフラグのみを更新
     * @param address 住所エンティティ
     * @return 影響を受けた行数
     */
    int updateDefaultFlag(UserAddress address);
    
    /**
     * 住所を削除
     * @param addressId 住所ID
     * @return 影響を受けた行数
     */
    int delete(@Param("addressId") Long addressId);
    
    /**
     * ユーザーのデフォルト住所設定をすべて解除
     * @param userId ユーザーID
     * @return 影響を受けた行数
     */
    int clearDefaultAddresses(@Param("userId") Long userId);
}