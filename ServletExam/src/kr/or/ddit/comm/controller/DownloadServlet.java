package kr.or.ddit.comm.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.or.ddit.comm.service.AtchFileServiceImpl;
import kr.or.ddit.comm.service.IAtchFileService;
import kr.or.ddit.comm.vo.AtchFileVO;

@WebServlet("/comm/download.do")
public class DownloadServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		long atchFileId = req.getParameter("fileId") != null ? Long.parseLong(req.getParameter("fileId")) : -1;
		int fileSn = req.getParameter("fileSn") != null ? Integer.parseInt(req.getParameter("fileSn")) : 1;
		
		// 파일 세부 정보 조회
		IAtchFileService fileService = AtchFileServiceImpl.getInstance();
		
		AtchFileVO atchFileVO = new AtchFileVO();
		atchFileVO.setAtchFileId(atchFileId);
		atchFileVO.setFileSn(fileSn);
		
		try {
			atchFileVO = fileService.getAtchFileDetail(atchFileVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// 파일 다운로드 처리를 위한 응답헤더 정보 설정하기
		resp.setContentType("application/octet-stream");
		// URL에는 공백문자를 포함할 수 없다. URLEncoding을 이용하여 인코딩 작업을
		// 하면 공백은 (+)로 표시되기 때문에 +문자를 공백문자인 %20으로 바꿔준다.
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(atchFileVO.getOrignlFileNm(), "UTF-8")
		.replaceAll("\\+", "%20") + "\"" );
		
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(atchFileVO.getFileStreCours()));
		BufferedOutputStream bos = new BufferedOutputStream(resp.getOutputStream());
		
		int c = 0;
		while ((c = bis.read()) != -1) {
				bos.write(c);
		}
		bis.close();
		bos.close();
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
