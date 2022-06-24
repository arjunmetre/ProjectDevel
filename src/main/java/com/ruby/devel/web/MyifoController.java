package com.ruby.devel.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ruby.devel.model.MemberDto;
import com.ruby.devel.service.impl.MyinfoMapper;

@Controller
public class MyifoController {
	
	@Autowired
	MyinfoMapper MImapper;
	
	//mypage id값 정보 mapping
	@GetMapping("/activity/mypage")
	public ModelAndView activity_mypage(
			@SessionAttribute String userKey)
	{
		/* System.out.println(userKey); */
		ModelAndView mview = new ModelAndView();
		
		//idx에 해당하는 dto 얻기
		MemberDto dto = MImapper.getData(userKey);
		mview.addObject("dto", dto);
		
		//포워드
		mview.setViewName("/activity/activity_myInfo");
		
		return mview;
	}
		
	//mypage update
	@PostMapping("/activity/update")
	public String update(
			@ModelAttribute MemberDto dto,
			@RequestParam MultipartFile imgUpload,
			HttpSession session)
	{			
		//이미지 저장 경로
		String path = session.getServletContext().getRealPath("/photo"); //webapp/photo에 바로 넣기

		//업로드 안하면 no라고 db에 저장
		if(imgUpload.getOriginalFilename().equals("")) 
			dto.setPhoto("no"); //db에 no로 저장
		else {
			dto.setPhoto(imgUpload.getOriginalFilename());

			//실제 업로드
			try {
				imgUpload.transferTo(new File(path+"\\"+imgUpload.getOriginalFilename()));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}		
		
		//세션에서 얻은 userKey dto에 저장
		String key = (String)session.getAttribute("userKey");
		dto.setMember_idx(key);
		
		//update
		MImapper.updateInfo(dto);
		
		//완료 후 마이페이지로 다시 이동
		return "redirect:mypage";
	}
	
	
	
}
