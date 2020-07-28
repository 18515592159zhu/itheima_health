package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 体检套餐服务
 */
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDao setmealDao;

    @Autowired
    private JedisPool jedisPool;
/*
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;


    @Value("${out_put_path}")//从属性文件读取输出目录的路径
    private String outputpath;
*/


    //新增套餐信息，同时需要关联检查组
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();
        //绑定套餐和检查组的多对多关系
        this.setSetmealAndCheckgroup(setmealId, checkgroupIds);
        //将图片名称保存到Redis集合中
        String fileName = setmeal.getImg();
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, fileName);
    }

    /*
        // 新增套餐信息，同时需要关联检查组
        public void add(Setmeal setmeal, Integer[] checkgroupIds) {
            setmealDao.add(setmeal);
            Integer setmealId = setmeal.getId();//获取套餐id
            this.setSetmealAndCheckgroup(setmealId, checkgroupIds);
            //完成数据库操作后需要将图片名称保存到redis
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, setmeal.getImg());

            //当添加套餐后需要重新生成静态页面（套餐列表页面、套餐详情页面）
            generateMobileStaticHtml();
        }


        //生成当前方法所需的静态页面
        public void generateMobileStaticHtml() {
            //在生成静态页面之前需要查询数据
            List<Setmeal> list = setmealDao.findAll();

            //需要生成套餐列表静态页面
            generateMobileSetmealListHtml(list);

            //需要生成套餐详情静态页面
            generateMobileSetmealDetailHtml(list);
        }


        //生成套餐列表静态页面
        public void generateMobileSetmealListHtml(List<Setmeal> list) {
            Map map = new HashMap();
            //为模板提供数据，用于生成静态页面
            map.put("setmealList", list);
            generteHtml("mobile_setmeal.ftl", "m_setmeal.html", map);
        }


        //生成套餐详情静态页面（可能有多个）
        public void generateMobileSetmealDetailHtml(List<Setmeal> list) {
            for (Setmeal setmeal : list) {
                Map map = new HashMap();
                map.put("setmeal", setmealDao.findById4Detail(setmeal.getId()));
                generteHtml("mobile_setmeal_detail.ftl", "setmeal_detail_" + setmeal.getId() + ".html", map);
            }
        }


        //通用的方法，用于生成静态页面
        public void generteHtml(String templateName, String htmlPageName, Map map) {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();//获得配置对象
            Writer out = null;
            try {
                //加载模版文件
                Template template = configuration.getTemplate(templateName);
                //构造输出流
                out = new FileWriter(new File(outputpath + "/" + htmlPageName));
                //输出文件
                template.process(map, out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    */
    // 分页查询
    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage, pageSize);
        Page<Setmeal> page = setmealDao.findByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //设置套餐和检查组多对多关系，操作t_setmeal_checkgroup
    public void setSetmealAndCheckgroup(Integer setmealId, Integer[] checkgroupIds) {
        if (checkgroupIds != null && checkgroupIds.length > 0) {
            Map<String, Integer> map = null;
            for (Integer checkgroupId : checkgroupIds) {
                map = new HashMap<>();
                map.put("setmealId", setmealId);
                map.put("checkgroupId", checkgroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
    }

    // 查询所有套餐
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

/*
   //查询套餐详情
    public Setmeal findById(Integer id) {
        //查询套餐表，将基本信息查询出来
        //根据套餐id查询关联的检查组，再将查询出的检查组集合赋值给套餐对象
        //根据检查组id查询关联的检查项集合，将集合赋值给检查组对象
        return setmealDao.findById4Detail(id);
    }
*/

    //根据套餐ID查询套餐详情（套餐基本信息、套餐对应的检查组信息、检查组对应的检查项信息）
    public Setmeal findById(int id) {
        return setmealDao.findById(id);
    }

    //查询套餐预约占比数据
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }
}
