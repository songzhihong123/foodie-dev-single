package com.imooc.service.center;

import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.pojo.vo.MyCommentVO;
import com.imooc.utils.PagedGridResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MyCommentsService {

    /**
     * 根据订单ID查询关联的商品.
     * @param orderId
     */
    public List<OrderItems> queryPendingComments(String orderId);


    /**
     * 保存用户的评论
     * @param orderId
     * @param userId
     * @param commentList
     */
    public void saveComments(String orderId,String userId,List<OrderItemsCommentBO> commentList);

    /**
     * 我的评价查询 分页
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyComments(String userId,Integer page,Integer pageSize);



}
