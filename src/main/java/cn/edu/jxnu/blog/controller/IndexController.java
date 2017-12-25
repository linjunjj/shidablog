package cn.edu.jxnu.blog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import cn.edu.jxnu.blog.commons.StringUtil;
import cn.edu.jxnu.blog.domin.Blog;
import cn.edu.jxnu.blog.domin.Blogger;
import cn.edu.jxnu.blog.domin.Link;
import cn.edu.jxnu.blog.domin.PageBean;
import cn.edu.jxnu.blog.service.BlogService;
import cn.edu.jxnu.blog.service.BlogTypeService;
import cn.edu.jxnu.blog.service.BloggerService;
import cn.edu.jxnu.blog.service.LinkService;

/**
 * @Description 主页Controller
 * 
 */
@Controller
public class IndexController {

	@Resource
	private BlogService blogService;
	@Resource
	private BloggerService bloggerService;
	@Resource
	private LinkService linkService;
	@Resource
	private BlogTypeService blogTypeService;

	/**
	 * @Description 请求主页
	 * @return
	 */
	@RequestMapping("/indexHome")
	public ModelAndView index(
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "typeId", required = false) String typeId,
			@RequestParam(value = "releaseDateStr", required = false) String releaseDateStr,
			HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		if (StringUtil.isEmpty(page)) {
			page = "1";
		}
		// 获取分页的bean
		PageBean<Blog> pageBean = new PageBean<Blog>(Integer.parseInt(page), 5); // 每页显示10条数据

		// map中封装起始页和每页的记录，按条件分类
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start", pageBean.getStart());
		map.put("end", pageBean.getEnd());
		map.put("typeId", typeId);
		map.put("releaseDateStr", releaseDateStr);
		// 获取博客信息
		List<Blog> blogList = blogService.listBlog(map);
		if (releaseDateStr != null) {
			Map<String, Object> map2 = new HashMap<>();
			map2.put("releaseDateStr", releaseDateStr);
			pageBean.setTotal(blogService.listBlog(map2).size());// 给前台总的记录数
		} else if (typeId != null) {
			Map<String, Object> map3 = new HashMap<String, Object>();
			map3.put("typeId", typeId);
			pageBean.setTotal(blogService.listBlog(map3).size());// 给前台总的记录数
		} else {
			pageBean.setTotal(blogService.listBlog(
					new HashMap<String, Object>()).size());// 给前台总的记录数
		}
		pageBean.setResult(blogList);
		for (Blog blog : blogList) {
			List<String> imageList = blog.getImageList();
			String blogInfo = blog.getContent(); // 获取博客内容
			Document doc = Jsoup.parse(blogInfo); // 将博客内容(网页中也就是一些html)转为jsoup的Document
			Elements jpgs = doc.select("img[src~=(jpg|jpeg|bmp|png)$]");// 获取<img>标签中所有后缀是.jpg的元素
			for (int i = 0; i < jpgs.size(); i++) {
				Element jpg = jpgs.get(i); // 获取到单个元素
				// System.out.println(jpg.toString().replace(">",
				// " style='width: 180px;height: 50px;'>"));
				imageList.add(jpg.toString().replace(">",
						" style='width: 184px;height: 97px;'>")); // 把图片信息存到imageList中
				if (i == 2)
					break; // 只存三张图片信息
			}
		}
		// 分页
		List<Link> linkList = linkService.getTotalData();
		modelAndView.setViewName("/indexViews/home");
		Blogger blogger = bloggerService.getBloggerData();
		ServletContext application = RequestContextUtils
				.findWebApplicationContext(request).getServletContext();
		application.setAttribute("blogger", blogger);
		application.setAttribute("pageBean", pageBean);
		application.setAttribute("linkList", linkList);
		List<Blog> blogCountList = blogService.countList();
		application.setAttribute("blogCountList", blogCountList); // 日期分档博客信息

		return modelAndView;

	}
}
