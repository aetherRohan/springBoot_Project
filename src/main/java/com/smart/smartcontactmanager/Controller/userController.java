package com.smart.smartcontactmanager.Controller;

import com.smart.smartcontactmanager.Configuration.EmailSenderService;
import com.smart.smartcontactmanager.Dao.ContactRepository;
import com.smart.smartcontactmanager.Dao.UserRepository;
import com.smart.smartcontactmanager.Entities.Contact;
import com.smart.smartcontactmanager.Entities.User;
import com.smart.smartcontactmanager.Helper.Message;
import com.smart.smartcontactmanager.Helper.Upload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@SessionAttributes("currentPage")
public class userController {

    @Autowired
   public EmailSenderService emailSenderService;
    @Autowired
   public UserRepository userRepository;
    @Autowired
    public ContactRepository contactRepository;
    @Autowired
   public Upload upload;

    @GetMapping("/dashboard")
    public String showDashboard(Principal principal,Model model){
         String userName=principal.getName();
         User user=this.userRepository.findByUsername(userName);
         model.addAttribute("name",user.getName());
        return "user/dashboard";  // the user is name of the users package in templates
    }


    @GetMapping("/add-contact")
    public String addContact(Model model,Principal principal) {
        String userName=principal.getName();
        User user=this.userRepository.findByUsername(userName);
        model.addAttribute("name",user.getName());

        model.addAttribute("contact", new Contact());

        return "user/add-contact";
    }



    @PostMapping("/process-contact")
    public String process_form(@ModelAttribute("contact") Contact contact, @RequestParam("image") MultipartFile file,
                               Principal principal) {

        try {
            String username = principal.getName();
            User user = this.userRepository.findByUsername(username);
            contact.setUser(user);
             this.upload.Uploader(file, contact);
            user.getContacts().add(contact);
            this.userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "user/contact-added";
    }


//pageNO= current page number
//how many contacts or object of contacts per page
@GetMapping("/show-contacts/{pageNo}")
public String showContacts(@PathVariable("pageNo") Integer pageNo, Model model, Principal principal,HttpSession session) {
    String userName = principal.getName(); /// got the userName
    User user = this.userRepository.findByUsername(userName);


    Pageable pageable = PageRequest.of(pageNo, 3);
    Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

    model.addAttribute("name",user.getName());

    model.addAttribute("contacts", contacts);
    model.addAttribute("currentPage",pageNo); // added to the session attributes now available throughout the controller
    model.addAttribute("totalPages",contacts.getTotalPages());
    model.addAttribute("totalContacts",contacts.getTotalElements());

    Message message = (Message) session.getAttribute("message");
    if (message != null) {
        model.addAttribute("message", message);// added to the model
        session.removeAttribute("message"); // removes after showing once
    }

    return "user/show-contacts";
}


    @GetMapping("/contact-profile/{Id}")
    public String contact_profile(@PathVariable("Id") Integer Id, Model model) {
        Optional<Contact> Optional_contact = this.contactRepository.findById(Id);
        Contact contact = Optional_contact.get();
        model.addAttribute("contact",contact);
        return "user/contact-profile";
    }


    @GetMapping("/delete/{Id}")
    public String Delete(@PathVariable("Id") Integer Id, Model model,
                         @ModelAttribute("currentPage")int currentPage,
                         HttpSession session,
                         Principal principal, HttpServletRequest request) {
        String userName = principal.getName();
        User user = this.userRepository.findByUsername(userName);

        Optional<Contact> optional = this.contactRepository.findById(Id);
        if (optional.isEmpty()) {
            //session.setAttribute("message", new Message("Contact not found", "Not Found"));
            return "redirect:/user/show-contacts/0";
        }
        Contact contact = optional.get();
        if (user.getId() == contact.getUser().getId()) {
            contact.setUser(null);
            this.contactRepository.delete(contact);
            String cURL= contact.getImageUrl();

            File file=new File("C:\\profile_pic\\"+cURL);
            if (file.exists() && file.isFile()){
                file.delete();
            }

            System.out.println(contact.getImageUrl());
            Message mssg = new Message("Deleted Successfully", "success");
            session.setAttribute("message", mssg);
        } else {
            session.setAttribute("message", new Message("Can not be Deleted", "Error"));
            return "redirect:/user/show-contacts/0";
        }
        Pageable pageable = PageRequest.of(0, 3);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
        int totalPage = contacts.getTotalPages();
        int lastPage = currentPage+1 > totalPage ? currentPage - 1 : currentPage;
        return "redirect:/user/show-contacts/" + lastPage;
    }


    @GetMapping("/update/{Id}")
    public String Update(@PathVariable("Id") Integer Id, Model model) {

        Optional<Contact> optional = this.contactRepository.findById(Id);
        Contact contact = optional.get();
        model.addAttribute("contact", contact);
        // opens the view
        return "user/update-contact";
    }

    //form action
    @PostMapping("/process-update")
    public String processUpdate(@ModelAttribute("contact") Contact contact, @RequestParam("image") MultipartFile file,
                                @RequestParam("imageUrl")String imageUrl, Principal principal) {
      String userName= principal.getName();
      User user=this.userRepository.findByUsername(userName);
        System.out.println("the ciD is :"+contact.getCId());

        try {
            if (!file.isEmpty()) {
                File file1 = new File("C:\\profile_pic\\" + contact.getImageUrl());
                if (file1.exists() && file1.isFile()) {
                    file1.delete();
                }
            }
            this.upload.Uploader(file, contact);
            // the form doesnt send the user inside the conatct again
            contact.setUser(user);
            this.contactRepository.save(contact);


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/user/show-contacts/0";
    }

    @PostMapping("/email-profile")
    public String emailProfile(@RequestParam("email")String email,Model model,
                               @ModelAttribute("currentPage")int page){

        model.addAttribute("email",email);
        model.addAttribute("page",page);
        return "user/email-profile";
    }

    @PostMapping("/send-email")
    public String processEmail(@RequestParam("email") String email,
                               @RequestParam("subject") String subject,
                               @RequestParam("body") String body,
                               @RequestParam("page")int page,HttpSession session) {

        this.emailSenderService.sendEmail(email, subject, body);
        Message message=new Message("Email Sent Succesfully","success");
        session.setAttribute("message",message);
        return "redirect:/user/show-contacts/"+page;
    }

}
