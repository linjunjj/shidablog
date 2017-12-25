package cn.edu.jxnu.blog.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.jxnu.blog.commons.ResponseUtil;
import cn.edu.jxnu.blog.domin.Blog;
import cn.edu.jxnu.blog.domin.Comment;
import cn.edu.jxnu.blog.service.BlogService;
import cn.edu.jxnu.blog.service.CommentService;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description 前台评论控制器 此控制器使用SpringBoot的@RestController。
 * 验证码为了方便放在java的session中
 */
@RestController
@RequestMapping(value = "/blog/comment")
public class CommentController {

	@Resource
	private CommentService commentService;
	@Resource
	private BlogService blogService;

	// 评论更新或者添加
	@RequestMapping(value = "save")
	public String save(Comment comment, HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			@RequestParam(value = "code", required = true) String code)
			throws Exception {

		// 四位数的String形式
		JSONObject result = new JSONObject();
		// 得到后台session的验证码
		String sessionCode = (String) session.getAttribute("code");
		if (!StringUtils.equalsIgnoreCase(code, sessionCode)) { // 忽略验证码大小写
			System.out.println("验证码对应不上code=" + code + "  sessionCode="
					+ sessionCode);
			result.put("success", false);
			result.put("errorInfo", "验证码错误！");
			ResponseUtil.write(response, result);
			return null;
		} else {
			int resultTotal = 0; // 执行记录数
			// 获取评论者ip
			String ip = request.getRemoteAddr();
			System.out.println("ip=" + ip);
			comment.setUserIp(ip);
			if (comment.getId() == null) {
				resultTotal = commentService.saveComment(comment); // 添加评论
				Blog blog = blogService.getById(comment.getBlog().getId()); // 更新一下博客的评论次数
				blog.setReplyHit(blog.getReplyHit() + 1);
				blogService.updateBlog(blog);
			} else {
				// 更新操作
			}

			if (resultTotal > 0) {
				result.put("success", true);
			}
			return result.toJSONString();
		}
	}

}
