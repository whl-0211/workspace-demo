/**
 * Copyright (C), 2019, 金科教育
 * FileName: SolrController
 * Author:   zyl
 * Date:     2019/11/11 14:35
 * History:
 * zyl          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jk.controller;

import com.jk.model.Car;
import com.jk.service.CarService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


@RequestMapping("solr")
@Controller
public class SolrController {

    @Autowired
    private CarService carService;


    @Autowired
    private SolrClient client;


    @RequestMapping("toadd")
    public String toadd(){

        return "addCar";
    }
    /**
     * 新增/修改 索引
     * 当 id 存在的时候, 此方法是修改(当然, 我这里用的 uuid, 不会存在的), 如果 id 不存在, 则是新增
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public void  add(Car car) {

       Integer addCar= carService.addCar(car);

        try {
            SolrInputDocument doc = new SolrInputDocument();
            doc.setField("id",addCar);
            doc.setField("carName", car.getCarName());
            doc.setField("carPrice", car.getCarPrice());
          //  doc.setField("carShow", car.getCarTime());
         //   doc.setField("carTime", car.getCarTime());
          //  doc.setField("carUrl", car.getCarUrl());

            /* 如果spring.data.solr.host 里面配置到 core了, 那么这里就不需要传 collection1 这个参数
             * 下面都是一样的
             */

            client.add("core1", doc);
            //client.commit();
            client.commit("core1");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /**
     * 根据id删除索引
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public void delete(Car car)  {
        carService.delete(car);
        try {
            client.deleteById("core1",car.getCarId().toString());
            client.commit("core1");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除所有的索引
     * @return
     */
    @RequestMapping("deleteAll")
    public String deleteAll(){
        try {

            client.deleteByQuery("core1","*:*");
            client.commit("core1");

            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * 根据id查询索引
     * @return
     * @throws Exception
     */
    @RequestMapping("getById")
    @ResponseBody
    public String getById(Car car) throws Exception {

        carService.getById(car.getCarId());

        SolrDocument document = client.getById("core1", car.getCarId().toString());
        System.out.println(document);
        return document.toString();
    }

    @RequestMapping("toSerch")
    public String toSerch(){

        return "index";
    }


    /**
     * 综合查询: 在综合查询中, 有按条件查询, 条件过滤, 排序, 分页, 高亮显示, 获取部分域信息
     * @return
     */
    @RequestMapping("search")
    @ResponseBody
    public Map<String, Object> search(Car car,Integer page,Integer rows){


        //返回到前台
        Map<String, Object> map1=new HashMap<>();

        try {
            //存放所有的查询条件
            SolrQuery params = new SolrQuery();

            //查询条件, 这里的 q 对应 下面图片标红的地方
            if(car.getCarName()!=null && !"".equals(car.getCarName())){
                params.set("q", car.getCarName());
            }else {
                params.set("q", "*:*");
            }


            //过滤条件
          // params.set("fq", "carPrice:["+car.get+" TO "++"]");

            //排序
            params.addSort("carPrice", SolrQuery.ORDER.desc);

            //分页
            if(page==null){
                params.setStart(0);
            }else {
                params.setStart((page-1)*rows);
            }
            if(rows==null){
                params.setRows(5);
            }else {
                params.setRows(rows);
            }


            //默认域
            params.set("df", "carName");

            //只查询指定域
            //params.set("fl", "id,product_title,product_price");

            //高亮
            //打开开关
            params.setHighlight(true);
            //指定高亮域
            params.addHighlightField("carName");
            //设置前缀
            params.setHighlightSimplePre("<span style='color:red'>");
            //设置后缀
            params.setHighlightSimplePost("</span>");

            //查询后返回的对象
            QueryResponse queryResponse = client.query("core1",params);
            //查询后返回的对象 获得真正的查询结果
            SolrDocumentList results = queryResponse.getResults();
            //查询的总条数
            long numFound = results.getNumFound();

            System.out.println(numFound);

         //获取高亮显示的结果, 高亮显示的结果和查询结果是分开放的
            Map<String, Map<String, List<String>>> highlight = queryResponse.getHighlighting();

            //创建list集合接收我们循环的参数
            List<Car> list1 =new ArrayList<>();
            for (SolrDocument result : results) {

                Car car1=new Car();
                String highFile="";

                Map<String, List<String>> map = highlight.get(result.get("id"));
                List<String> list = map.get("carName");
                if(list==null){
                    highFile= result.get("carName").toString();
                }else {
                    highFile=list.get(0);
                }

                car1.setCarId(Integer.parseInt(result.get("id").toString()));
                //car1.setCarUrl((String)result.get("carUrl"));
                //car1.setCarShow(result.get("carShow").toString());
                car1.setCarPrice(Double.parseDouble(result.get("carPrice").toString()));
                car1.setCarName(highFile);

                list1.add(car1);
            }
            map1.put("total",numFound);
            map1.put("rows",list1);
            return map1;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

}






