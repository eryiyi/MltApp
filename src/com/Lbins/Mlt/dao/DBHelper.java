package com.Lbins.Mlt.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.Lbins.Mlt.dao.dao.DaoMaster;
import com.Lbins.Mlt.dao.dao.HotWordObjDao;
import com.Lbins.Mlt.dao.dao.RecordMsgDao;
import com.Lbins.Mlt.dao.dao.ShoppingCartDao;
import com.Lbins.Mlt.module.HotWordObj;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

public class DBHelper {
    private static Context mContext;
    private static DBHelper instance;
    private static DaoMaster.DevOpenHelper helper;
    private ShoppingCartDao testDao;
    private RecordMsgDao recordDao;
    private HotWordObjDao hotWordObjDao;
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;

    private DBHelper() {
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper();
            if (mContext == null) {
                mContext = context;
            }
            helper = new DaoMaster.DevOpenHelper(context, "mlt_db_t", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            instance.testDao = daoMaster.newSession().getShoppingCartDao();
            instance.recordDao = daoMaster.newSession().getRecordMsgDao();
            instance.hotWordObjDao = daoMaster.newSession().getHotWordObjDao();
        }
        return instance;
    }

    /**
     * 插入数据
     *
     * @param test
     */
    public void addShoppingToTable(ShoppingCart test) {
        testDao.insert(test);
    }

    //查询是否存在该商品
    public boolean isSaved(String ID) {
        QueryBuilder<ShoppingCart> qb = testDao.queryBuilder();
        qb.where(ShoppingCartDao.Properties.Goods_id.eq(ID));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }

    //批量插入数据
    public void saveTestList(List<ShoppingCart> tests) {
        testDao.insertOrReplaceInTx(tests);
    }

    /**
     * 查询所有的购物车
     *
     * @return
     */
    public List<ShoppingCart> getShoppingList() {
        return testDao.loadAll();
    }

    /**
     * 插入或是更新数据
     *
     * @param test
     * @return
     */
    public long saveShopping(ShoppingCart test) {
        return testDao.insertOrReplace(test);
    }

    /**
     * 更新数据
     *
     * @param test
     */
    public void updateTest(ShoppingCart test) {
        testDao.update(test);
    }

//    /**
//     * 获得所有收藏的题
//     * @return
//     */
//    public List<ShoppingCart> getFavourTest(){
//        QueryBuilder qb = testDao.queryBuilder();
//        qb.where(ShoppingCartDao.Properties.IsFavor.eq(true));
//        return qb.list();
//    }

    /**
     * 删除所有数据--购物车
     */

    public void deleteShopping() {
        testDao.deleteAll();
    }

    /**
     * 删除数据根据goods_id
     */

    public void deleteShoppingByGoodsId(String cartid) {
        QueryBuilder qb = testDao.queryBuilder();
        qb.where(ShoppingCartDao.Properties.Cartid.eq(cartid));
        testDao.deleteByKey(cartid);//删除
    }


    //动态
    //批量插入数据
    public void saveRecordList(List<RecordMsg> tests) {
        recordDao.insertOrReplaceInTx(tests);
    }

    /**
     * 查询动态列表
     *
     * @return
     */
    public List<RecordMsg> getRecordList() {
        return recordDao.loadAll();
    }

    /**
     * 插入或是更新数据
     *
     * @param test
     * @return
     */
    public long saveRecord(RecordMsg test) {
        return recordDao.insertOrReplace(test);
    }

    //查询是否存在该动态
    public boolean isRecord(String id) {
        QueryBuilder<RecordMsg> qb = recordDao.queryBuilder();
        qb.where(RecordMsgDao.Properties.Mm_msg_id.eq(id));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }

    //查询动态
    public RecordMsg getRecord(String id) {
        RecordMsg recordMsg = recordDao.load(id);

        return recordMsg;
    }

    /**
     * 更新数据
     *
     * @param test
     */
    public void updateRecord(RecordMsg test) {
        recordDao.update(test);
    }

    /**
     * 插入或是更新数据
     *
     * @param hotWordObj
     * @return
     */
    public long saveWord(HotWordObj hotWordObj) {
        return hotWordObjDao.insertOrReplace(hotWordObj);
    }


    public List<HotWordObj> getWords(){
        QueryBuilder qb = hotWordObjDao.queryBuilder();
        return qb.list();
    }
}
