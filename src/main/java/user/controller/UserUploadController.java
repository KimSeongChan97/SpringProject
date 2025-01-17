package user.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import user.bean.UserUploadDTO;
import user.service.ObjectStorageService;
import user.service.UserUploadService;

@Controller
@RequestMapping(value="user")  // 모든 요청이 "user" 경로로 들어오도록 매핑
public class UserUploadController {

	@Autowired
	private UserUploadService userUploadService;
	// 업로드된 파일 데이터를 처리하는 `UserUploadService` 객체를 주입받습니다.
	// `UserUploadService`는 파일 데이터를 데이터베이스에 저장하거나 조회하는 역할을 합니다.

	@Autowired
	private ObjectStorageService objectStorageService;
	// 파일을 클라우드 Object Storage에 업로드하거나 삭제하는 `ObjectStorageService` 객체를 주입받습니다.
	// 주로 Naver Cloud Platform과 같은 Object Storage 서비스를 사용합니다.

	private String bucketName = "bitcamp-9th-bucket-65";
	// Object Storage에서 사용하는 버킷 이름입니다.
	// 버킷은 파일을 저장하는 공간이며, 여러 개의 버킷을 사용할 수 있습니다.

	@RequestMapping(value="uploadForm")
	public String uploadForm() {
		// 업로드 폼 페이지로 이동하는 메서드입니다.
		// 사용자가 파일을 업로드할 수 있는 HTML 폼을 보여주는 역할을 합니다.
		return "/upload/uploadForm";  // 업로드 폼 페이지를 반환
	}

	@RequestMapping(value="uploadAJaxForm")
	public String uploadAJaxForm() {
		// Ajax 방식으로 파일을 업로드할 수 있는 업로드 폼 페이지로 이동합니다.
		return "/upload/uploadAJaxForm";  // Ajax를 사용한 업로드 폼 페이지를 반환
	}

	// 1개 또는 여러 개(드래그)를 업로드하는 경우 처리
	// 파일명에 한글 또는 공백이 있어도 업로드가 가능하다. (produces = "text/html; charset=UTF-8")
	@RequestMapping(value="upload", method=RequestMethod.POST, produces = "text/html; charset=UTF-8")
	@ResponseBody  // 결과를 HTTP 응답 본문에 직접 쓰는 역할을 합니다.
	public String upload(@ModelAttribute UserUploadDTO userUploadDTO,
						 @RequestParam("img[]") List<MultipartFile> list,
						 HttpSession session) {
		// 실제 폴더 경로를 가져옵니다.
		// 서버 상의 실제 경로로 파일을 저장하기 위한 절대 경로를 구하는 방법입니다.
		String filePath = session.getServletContext().getRealPath("WEB-INF/storage");  
		// `session.getServletContext().getRealPath()`는 웹 애플리케이션에서 실제 파일이 저장되는 경로를 반환합니다.
		// 이 경로는 서버가 파일을 저장하는 디렉토리입니다.
		System.out.println("실제 폴더 = " + filePath);
		// 실제 저장 경로는 로컬 서버 환경에 따라 다를 수 있으며, 배포 환경에 따라 달라질 수 있습니다.

		String imageFileName;
		String imageOriginalFileName;
		File file;
		String result = "";

		// 업로드된 파일 정보를 저장할 리스트를 생성합니다.
		List<UserUploadDTO> imageUploadList = new ArrayList<>();
		// 업로드된 여러 개의 파일을 처리하기 위한 `MultipartFile` 리스트를 순회하며 처리합니다.

		for (MultipartFile img : list) {  
			// 각 파일에 대해 UUID를 사용하여 고유 파일 이름을 생성할 수도 있습니다.
			// imageFileName = UUID.randomUUID().toString();  // UUID로 고유 파일명을 생성합니다.
            
			// Object Storage에 파일을 업로드하고 저장된 파일 이름을 반환받습니다.
			imageFileName = objectStorageService.uploadFile(bucketName, "storage/", img);
			// `objectStorageService.uploadFile` 메서드를 통해 파일을 Object Storage에 업로드합니다.
			// 파일이 저장된 경로와 파일 이름이 반환됩니다.
				
			// 업로드된 파일의 원본 파일 이름을 가져옵니다.
			imageOriginalFileName = img.getOriginalFilename();
			// 원본 파일명은 사용자가 업로드한 파일의 실제 이름입니다.
			
			// 서버의 실제 파일 경로에 해당 파일을 저장할 `File` 객체를 생성합니다.
			file = new File(filePath, imageOriginalFileName);  
			
			try {
				// 파일을 지정한 경로로 실제 저장합니다.
				img.transferTo(file);
				// `transferTo` 메서드는 업로드된 파일을 지정한 경로에 복사하여 실제 파일을 저장합니다.
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// 업로드된 파일을 표시하기 위한 HTML을 생성합니다.
			result += "<span>"
					+ "<img src='/spring/storage/"
					+ imageOriginalFileName
					+ "' width='200' height='200'>"
					+ "</span>";
			// 사용자가 업로드한 이미지를 미리보기로 보여주기 위한 HTML입니다.
			// '/spring/storage/'는 파일을 불러올 수 있는 가상의 경로이며, 실제 파일은 서버 내부의 WEB-INF에 저장됩니다.
			
			// 업로드된 파일 정보를 DTO에 저장하고, 리스트에 추가합니다.
			UserUploadDTO dto = new UserUploadDTO();
			dto.setImageName(userUploadDTO.getImageName());
			dto.setImageContent(userUploadDTO.getImageContent());
			dto.setImageFileName(imageFileName);  // Object Storage에 저장된 파일명을 설정
			dto.setImageOriginalFileName(imageOriginalFileName);  // 업로드된 원본 파일명을 설정

			// DTO를 리스트에 추가하여 나중에 DB에 저장될 수 있도록 준비합니다.
			imageUploadList.add(dto);
		}

		// 업로드된 파일 정보를 출력 (서버 콘솔에서 확인 가능)
		System.out.println(userUploadDTO.getImageName() + ", "
					+ userUploadDTO.getImageContent() + ", "
					+ userUploadDTO.getImageFileName() + ", "
					+ userUploadDTO.getImageOriginalFileName());

		// 업로드된 파일 정보들을 DB에 저장하는 작업을 처리합니다.
		userUploadService.upload(imageUploadList);
		// `upload` 메서드는 업로드된 파일 메타데이터를 데이터베이스에 저장하는 역할을 합니다.

		return result;  // 업로드된 이미지의 결과 HTML을 반환하여 클라이언트에게 전송합니다.
	}
	
	// 업로드된 파일 목록을 조회하는 메서드
	@RequestMapping(value="uploadList")
	public ModelAndView uploadList() {
	    // `uploadList` 메서드를 호출하여 데이터베이스에 저장된 파일 목록을 가져옵니다.
	    List<UserUploadDTO> list = userUploadService.uploadList();

	    // ModelAndView 객체를 생성하여 데이터를 담습니다.
	    ModelAndView mav = new ModelAndView();
	    mav.addObject("list", list);  
	    // 가져온 파일 목록을 "list"라는 이름으로 모델에 담아 JSP로 전달합니다.

	    mav.setViewName("/upload/uploadList"); 
	    // `/upload/uploadList`는 파일 목록을 보여주는 JSP 페이지입니다.
	    
	    return mav;  // ModelAndView 객체를 반환하여, 뷰 이름과 함께 데이터를 전달합니다.
	}
	
	// 특정 파일의 세부 정보를 조회하는 메서드
	@RequestMapping(value="uploadView")
	public String uploadView(@RequestParam String seq, Model model) {
		// `seq`는 파일의 고유 번호로, 이를 사용하여 해당 파일의 상세 정보를 조회합니다.
		UserUploadDTO userUploadDTO = userUploadService.getuploadDTO(seq);
		
		// 모델에 `userUploadDTO` 객체를 추가하여 뷰로 전달합니다.
		model.addAttribute("userUploadDTO", userUploadDTO);
		
		return "/upload/uploadView";  // 파일 상세 정보를 보여주는 뷰 페이지로 이동합니다.
	}
	
	// 파일 업데이트 폼으로 이동하는 메서드
	@RequestMapping(value="uploadUpdateForm")
	public String uploadUpdateForm(@RequestParam String seq, Model model) {
		// 파일의 `seq` 번호를 사용하여 데이터베이스에서 해당 파일 정보를 조회합니다.
		UserUploadDTO userUploadDTO = userUploadService.getuploadDTO(seq); // 1개의 정보를 담고 감
		
		// 모델에 `userUploadDTO` 객체를 추가하여 업데이트 폼으로 전달합니다.
		model.addAttribute("userUploadDTO", userUploadDTO);
		
		return "/upload/uploadUpdateForm";  // 업로드 수정 폼 페이지로 이동
	}
	
	// 파일 업데이트 처리
	@RequestMapping(value="uploadUpdate", produces = "text/html; charset=UTF-8")
	@ResponseBody  // 결과를 HTTP 응답 본문에 직접 씁니다.
	public String uploadUpdate(@ModelAttribute UserUploadDTO userUploadDTO,
			 			  @RequestParam("img") MultipartFile img){
		
		// 수정된 파일 정보를 DB와 Object Storage에 반영합니다.
		userUploadService.uploadUpdate(userUploadDTO, img);
		
		return "이미지 수정 완료";  // 업데이트 완료 메시지를 반환합니다.
	}
	
	// 파일 선택한 것 삭제
	@RequestMapping(value="uploadDelete")
	@ResponseBody
	// @RequestMapping 어노테이션은 이 메서드가 "/uploadDelete" URL로 들어오는 HTTP 요청을 처리한다는 것을 나타냅니다.
	// value="uploadDelete"는 클라이언트에서 이 URL로 요청이 들어올 때 해당 메서드를 호출하도록 매핑합니다.
	// @ResponseBody는 이 메서드가 결과를 HTTP 응답으로 직접 반환한다는 의미입니다. 
	// 여기서는 반환 값이 없지만, 이 어노테이션은 메서드가 HTML 페이지가 아닌 데이터를 반환할 때 사용됩니다.
	public void uploadDelete(@RequestParam String[] check) {
	    // @RequestParam 어노테이션은 클라이언트에서 전달된 파라미터를 받아오는 역할을 합니다.
	    // 여기서는 'check'라는 이름의 배열을 받아옵니다. 
	    // 이 배열은 사용자가 삭제하려고 선택한 파일들의 ID를 담고 있습니다.
	    
	    for(String seq : check) {
	        // 사용자가 선택한 파일들의 ID를 하나씩 순회하면서 처리합니다.
	        // for문을 사용해 check 배열에 담긴 각 ID를 순차적으로 처리합니다.
	        
	        // 각 ID(seq)를 출력하여 실제로 어떤 파일이 삭제될 예정인지 확인합니다.
	        // 이 출력은 디버깅 용도로, 삭제할 항목의 ID를 콘솔에 출력합니다.
	        System.out.println(" 삭제할 seq 번호 = " + seq);
	        // seq는 삭제할 파일의 고유 번호를 의미합니다.
	        // 이 번호는 데이터베이스 또는 파일 시스템에서 파일을 식별하는 데 사용됩니다.
	        
	    } // for
	    
	    // userUploadService.uploadDelete(check)는 서비스 계층에서 파일 삭제를 처리하는 메서드입니다.
	    // check 배열에는 삭제할 파일 ID들이 담겨 있으며, 이 배열을 서비스에 넘겨 해당 파일을 삭제하도록 요청합니다.
	    // 서비스 계층은 비즈니스 로직을 처리하는 역할을 하며, 데이터베이스나 파일 시스템에서 실제 삭제 작업을 수행합니다.
	    userUploadService.uploadDelete(check);
	    // 이 메서드는 반환 값이 없습니다(void). 
	    // 클라이언트 측에서는 이 메서드를 호출하여 파일 삭제를 요청하며, 성공적으로 삭제가 완료되면 별도의 응답을 받지 않습니다.
	    
	} // uploadDelete
	
	
	
}

// Mapper -> DAO Interface -> ServiceImpl -> Service Interface -> controller

/*
@ResponseBody ?
@ResponseBody는 메서드가 반환하는 값을 view를 통해서가 아니라, 
HTTP 응답 본문에 직접 쓰는 역할을 합니다. 
주로 AJAX나 RESTful 웹 서비스에서 사용되며, JSON, XML 또는 문자열 데이터를 반환할 때 사용됩니다.
*/
