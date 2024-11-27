package com.petalaura.admin.controller;

import com.petalaura.library.Service.CategoryService;
import com.petalaura.library.Service.ImageService;
import com.petalaura.library.Service.ProductService;
import com.petalaura.library.dto.CategoryDto;
import com.petalaura.library.dto.ProductDto;
import com.petalaura.library.model.Category;
import com.petalaura.library.model.Image;
import com.petalaura.library.model.Product;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ProductController {

   @Autowired
    private ProductService productService;
   @Autowired
   private CategoryService categoryService;
   @Autowired
   private ImageService imageService;

    @GetMapping("/products")
    public String products(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        List<ProductDto> productDtoList = productService.findAll();
        for (ProductDto product : productDtoList) {
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                System.out.println("Image file name: " + product.getImage().get(0).getName());
            } else {
                System.out.println("No image available for product: " + product.getName());
            }
        }
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", categories);
        model.addAttribute("title","Manage Product");
        model.addAttribute("size", productDtoList.size());
        model.addAttribute("products", productDtoList);
        return "products-list";
    }

    @GetMapping("/add-product")
        public String addProductPage(Model model) {

           model.addAttribute("title", "Add Product");
           List<Category> categories = categoryService.findAllByActivatedTrue();
           model.addAttribute("categories", categories);
           model.addAttribute("categoryNew", new CategoryDto());
           model.addAttribute("productDto", new ProductDto());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "add-product";
    }
    @PostMapping("/save-product")
    public String saveProduct(@Valid @ModelAttribute("productDto")ProductDto productDto,
                              BindingResult result,
                              @RequestParam("imageProduct") List<MultipartFile> imageProduct,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        if (result.hasErrors()) {
          //  model.addAttribute("productDto", productDto);
            // result.toString();
            return "redirect:/add-product";
        } else {
            try {

                productService.save(imageProduct, productDto);
                redirectAttributes.addFlashAttribute("success", "Added new product successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Failed to add new product!");
            }
            return "redirect:/products/0";
        }
    }

    @GetMapping("/products/{pageNo}")
    public String allProducts(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Page<Product> products = productService.pageProducts(pageNo);
        List<Category> categories = categoryService.findAllByActivatedTrue();

        model.addAttribute("categories", categories);
        model.addAttribute("title", "Manage Products");
        model.addAttribute("products", products);
        model.addAttribute("size", products.getTotalElements());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());
        return "products-list";
    }




    @GetMapping("/search-products/{pageNo}")
    public String searchProduct(@PathVariable("pageNo") int pageNo,
                                @RequestParam("keyword") String keyword,
                                Model model, Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }
        Page<ProductDto> products = productService.searchProducts(pageNo, keyword);
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", categories);
        model.addAttribute("title", "Search Result");
        model.addAttribute("products", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());
        return "products-result";

    }

    @GetMapping("/update-product/{id}")
    public String updateProductForm(@PathVariable("id") Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        List<Category> categories = categoryService.findAllByActivatedTrue();
        ProductDto productDto = productService.getById(id);
        List<Image> images =imageService.findProductImages(id);
        model.addAttribute("title", "Add Product");
        model.addAttribute("categories", categories);
        model.addAttribute("images", images);
        model.addAttribute("productDto", productDto);
        return "update-product";
    }
    //
    @PostMapping("/update-product/{id}")
    public String updateProduct(@PathVariable("id") Long id,
                                @ModelAttribute("productDto") ProductDto productDto,
                                @RequestParam("imageProduct") List<MultipartFile> imageProduct,
                                RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            productService.update(imageProduct, productDto);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
        }
        return "redirect:/products/0";
    }



    @RequestMapping(value = "/enable-product/{id}", method = {RequestMethod.PUT, RequestMethod.GET})
    public String enabledProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            productService.enableById(id);
            redirectAttributes.addFlashAttribute("success", "Enabled successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Enabled failed!");
        }
        return "redirect:/products/0";
    }

    @RequestMapping(value = "/delete-product/{id}", method = {RequestMethod.PUT, RequestMethod.GET})
    public String deletedProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Deleted failed!");
        }
        return "redirect:/products/0";
    }


   @GetMapping("/high-price")
    public String filterHighPrice(Model model){
     List<CategoryDto> categories = categoryService.getCategoriesAndSize();
     model.addAttribute("categories", categories);
     //List<ProductDto> products=productService.filterHighProducts(  );
     List<ProductDto>  listView=productService.listViewProducts();
       model.addAttribute("title", "Shop Detail");
       model.addAttribute("page", "Shop Detail");
       model.addAttribute("productViews", listView);
      // model.addAttribute("products", products);
       return "shop-list";


   }

}
