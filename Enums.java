package quizGenerator;

public class Enums {
	public enum Level{
		HARD(0,"HARD"),
		MEDIUM(1,"MEDIUM"),
		EASY(2,"EASY");
		
		private Integer id;
		private String name;
		
		Level(final Integer id, final String name){
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public enum Tags{
		Tag1(0,"Tag1"),
		Tag2(1,"Tag2"),
		Tag3(2,"Tag3"),
		Tag4(3,"Tag4"),
		Tag5(4,"Tag5"),
		Tag6(5,"Tag6");
		
		private Integer id;
		private String name;
		
		Tags(final Integer id, final String name){
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public static Integer toTags(String value) {
			if (value != null && value.isEmpty() == false) {
				for (Tags tag : Tags.values()) {
					if (value.equalsIgnoreCase(tag.getName())) {
						return tag.getId();
					}
				}
			}
			return null;
		}
	}
}
