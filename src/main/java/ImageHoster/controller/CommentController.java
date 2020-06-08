package ImageHoster.controller;

import ImageHoster.model.Comment;
import ImageHoster.model.Image;
import ImageHoster.model.User;
import ImageHoster.service.CommentService;
import ImageHoster.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private CommentService commentService;

    /*This controller method is called when the request pattern is of type '/image/{imageId}/{title}/comments'
      The method redirects to '/images/{imageId}/{title}' URL mapped to showImage method of ImageController
      This method takes user added comment as a request parameter and creates a new comment object and adds comment to commentList in Image.
      If the comment text is null, user is simply redirected to same image page*/
    @RequestMapping(value = "/image/{imageId}/{title}/comments",method = RequestMethod.POST)
    public String createComment(@PathVariable("title") String title, @PathVariable("imageId") Integer imageId, @RequestParam("comment") String comment, HttpSession session, Model model) {

        //getting logged-in user to set the user attribute of comment
        User user = (User) session.getAttribute("loggeduser");
        //getting image to set image attribute of comment
        Image image = imageService.getImage(imageId);
        //create a new comment object to set attribute values and persist in DB.
        Comment newComment = new Comment();
        //Settings values to all attributes of newComment object
        newComment.setCreatedDate(new Date());
        newComment.setImage(image);
        newComment.setUser(user);
        newComment.setText(comment);

        //Checking if null comment is being uploaded, if so redirect to same page.Do not allow user to add blank comments
        String blankComment = newComment.getText();
        if (!blankComment.isEmpty()) {

            //updating commentList of this particular image with the new comment from user
            List<Comment> commentList = image.getCommentList();
            commentList.add(newComment);
            image.setCommentList(commentList);

            //adding commentList to key comments,logged-in user to key user in model which is passed to showImage method of ImageController
            model.addAttribute("comments", commentList);
            model.addAttribute("user", user);


            //calling uploadComment method in commentService to process the comment and persist in database.
            commentService.uploadComment(newComment);
            //redirect user to same page displaying all details of the particular image.
            return "redirect:/images/{imageId}/{title}";
        }
        //redirect user to same page, do not add empty comments to commentList
        return "redirect:/images/{imageId}/{title}";
    }

}
