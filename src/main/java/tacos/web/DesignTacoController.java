package tacos.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import tacos.data.Ingredient;
import tacos.data.Ingredient.Type;
import tacos.data.IngredientRepository;
import tacos.data.Order;
import tacos.data.Taco;
import tacos.data.TacoRepository;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {
	
	private final IngredientRepository ingredientRepository;
	
	private final TacoRepository designRepo;
	
	@Autowired
	public DesignTacoController(IngredientRepository ingredientRepository,TacoRepository designRepo) {
		this.ingredientRepository = ingredientRepository;
		this.designRepo = designRepo;
	}
	
	@ModelAttribute
	public void addIngredientsToModel(Model model) {
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepository.findAll().forEach(ingredients::add);
		
		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
		  model.addAttribute(type.toString().toLowerCase(),
		      filterByTypes(ingredients, type));
		}
	}
	
	@ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}

	@GetMapping
	public String showDesignForm(Model model) {
		model.addAttribute("design",new Taco());
		return "design";
	}
	
	@PostMapping
	public String processDesign(@Valid  @ModelAttribute("design") Taco design,Errors error,Order order) {
		if(error.hasErrors()) {
			return "design";
		}
		Taco saved = designRepo.save(design);
		order.addDesign(saved);
		log.info("Process Design::"+design);
		return "redirect:/orders/current";
	}
	
	private List<Ingredient> filterByTypes(List<Ingredient> ingredients,Type type) {
		return ingredients
					.stream()
					.filter(x -> x.getType().equals(type))
					.toList();
	}
}
