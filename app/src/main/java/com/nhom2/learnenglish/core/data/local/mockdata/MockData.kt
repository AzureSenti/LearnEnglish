package com.nhom2.learnenglish.core.data.local.mockdata

import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity
import com.nhom2.learnenglish.core.data.local.entity.StoryEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarLessonEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarQuestionEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity

object MockData {

    // Từ vựng
    val wordSets = listOf(
        WordSetEntity(id = "1", name = "Giao tiếp cơ bản", description = "Các câu chào hỏi và từ vựng thông dụng hàng ngày.", unlockCost = 0),
        WordSetEntity(id = "2", name = "Công nghệ thông tin", description = "Từ vựng chuyên ngành lập trình và phần cứng.", unlockCost = 100),
        WordSetEntity(id = "3", name = "Kinh doanh & Tài chính", description = "Từ vựng về hợp đồng, đầu tư và đàm phán.", unlockCost = 200),
        WordSetEntity(id = "4", name = "Du lịch & Khám phá", description = "Hành lý, sân bay và các địa điểm tham quan.", unlockCost = 50)
    )

    val words = listOf(
        WordEntity(id = "1", englishWord = "Hello", vietnameseMeaning = "Xin chào", audio = "hello_audio_url"),
        WordEntity(id = "2", englishWord = "Thank you", vietnameseMeaning = "Cảm ơn", audio = "thanks_audio_url"),
        WordEntity(id = "3", englishWord = "Please", vietnameseMeaning = "Vui lòng / Làm ơn", audio = "please_audio_url"),
        WordEntity(id = "4", englishWord = "Sorry", vietnameseMeaning = "Xin lỗi", audio = "sorry_audio_url"),
        WordEntity(id = "5", englishWord = "Goodbye", vietnameseMeaning = "Tạm biệt", audio = "goodbye_audio_url"),

        WordEntity(id = "6", englishWord = "Database", vietnameseMeaning = "Cơ sở dữ liệu", audio = "db_audio_url"),
        WordEntity(id = "7", englishWord = "Algorithm", vietnameseMeaning = "Thuật toán", audio = "algo_audio_url"),
        WordEntity(id = "8", englishWord = "Application", vietnameseMeaning = "Ứng dụng", audio = "app_audio_url"),
        WordEntity(id = "9", englishWord = "Variable", vietnameseMeaning = "Biến số", audio = "var_audio_url"),
        WordEntity(id = "10", englishWord = "Server", vietnameseMeaning = "Máy chủ", audio = "server_audio_url"),

        WordEntity(id = "11", englishWord = "Contract", vietnameseMeaning = "Hợp đồng", audio = "contract_audio_url"),
        WordEntity(id = "12", englishWord = "Investment", vietnameseMeaning = "Đầu tư", audio = "invest_audio_url"),
        WordEntity(id = "13", englishWord = "Negotiation", vietnameseMeaning = "Đàm phán", audio = "nego_audio_url"),
        WordEntity(id = "14", englishWord = "Profit", vietnameseMeaning = "Lợi nhuận", audio = "profit_audio_url"),
        WordEntity(id = "15", englishWord = "Bankruptcy", vietnameseMeaning = "Phá sản", audio = "bankrupt_audio_url")
    )

    val wordSetRefs = listOf(
        WordSetCrossRef("1", "1"), WordSetCrossRef("2", "1"), WordSetCrossRef("3", "1"), WordSetCrossRef("4", "1"), WordSetCrossRef("5", "1"),
        WordSetCrossRef("6", "2"), WordSetCrossRef("7", "2"), WordSetCrossRef("8", "2"), WordSetCrossRef("9", "2"), WordSetCrossRef("10", "2"),
        WordSetCrossRef("11", "3"), WordSetCrossRef("12", "3"), WordSetCrossRef("13", "3"), WordSetCrossRef("14", "3"), WordSetCrossRef("15", "3")
    )
    val articles = listOf(
        ArticleEntity(
            title = "A Saharan Spinosaurus",
            content = "Today, Jenguebi is dry and barren, with few trees and endless sand. The local Tuareg community calls the area where the fossils were found Sirig Taghat, which translates to “No water, no goat.”\n" +
                    "\n" +
                    "Sereno was drawn to the Sahara because of a report by French geologist Hugues Faure in the 1950s of a dinosaur tooth he found in Niger and wanted to search for similar sites.\n" +
                    "\n" +
                    "“I knew it was the needle in the haystack,” Sereno says of the remote fossil site. “It could easily have been swallowed by the sand.”\n" +
                    "\n" +
                    "Led by a local guide named Abdul Nasser riding on a moped across the desert, Sereno and Vidal first scouted the site in 2019 and found a Spinosaurus jawbone, along with a handful of other fossils. After returning in 2022, they ultimately identified bones from three S. mirabilis individuals, along with another predatory dinosaur named Carcharodontosaurus, two long-necked sauropod dinosaurs, crocodiles, turtles, and a freshwater fish species that could reach 12 feet long.",
            level = "B2 Intermediate",
            category = "Business",
            readTime = "5 min read",
            isCompleted = true,
            image = "https://images.unsplash.com/photo-1633356122544-f134324a6cee?q=80&w=1000&auto=format&fit=crop"
        ),

        // Báo
        ArticleEntity(
            title = "China develops ultra-low-cost iron battery that retains 99.4% efficiency for over 16 years",
            content = "Chinese scientists have developed an iron battery 80 times cheaper than lithium that can run for over 6,000 cycles with almost no loss in capacity.\n" +
                    "The \"all-iron flow battery\" technology was developed by a research team from the Institute of Metal Research under the Chinese Academy of Sciences, and their findings were published earlier this month in the journal Advanced Energy Materials.\n" +
                    "\n" +
                    "Unlike the lithium-ion batteries commonly used today, all-iron flow batteries rely on iron, which is far cheaper and more widely available, with lithium currently trading at more than 80 times the cost of iron.\n" +
                    "\n" +
                    "The large price gap makes iron-based batteries a possible solution to supply constraints slowing the global shift to green energy, according to Interesting Engineering.\n" +
                    "\n" +
                    "The main challenge for iron flow batteries has been their short lifespan. Over time, the battery’s active materials can break down or leak through internal membranes, reducing efficiency and shortening operational life.\n" +
                    "\n" +
                    "To solve this, the Chinese team designed a new iron compound that acts like a protective shield. It helps prevent damage to the battery’s core materials and stops them from leaking across the membrane, as reported by the South China Morning Post.\n",

            level = "B2 Intermediate",
            category = "Business",
            readTime = "5 min read",
            isCompleted = false,
            image = "https://images.unsplash.com/photo-1633356122544-f134324a6cee?q=80&w=1000&auto=format&fit=crop"
        ),
        ArticleEntity(
            title = "The Evolution of Full-Stack Web Development",
            content = "In recent years, full-stack web development has seen a massive shift. Technologies like React and Next.js have revolutionized how developers build interactive user interfaces. On the server side, Node.js and Java Spring Boot continue to power robust and scalable backends.\n\nDatabase management has also evolved, with developers utilizing both relational databases like MySQL and modern cloud solutions like Supabase or PostgreSQL to ensure data integrity and real-time synchronization. As system requirement specifications become more complex, mastering these tools is essential for any modern developer.",
            level = "C1 Advanced",
            category = "Technology",
            readTime = "4 min read",
            isCompleted = false,
            image = "https://images.unsplash.com/photo-1633356122544-f134324a6cee?q=80&w=1000&auto=format&fit=crop"
        ),
        ArticleEntity(
            title = "Why Cozy Games and Epic Adventures Capture Our Hearts",
            content = "The gaming industry offers diverse experiences that cater to different player moods. On one hand, cozy farming simulators like Stardew Valley provide a relaxing escape, allowing players to build relationships with town characters, harvest crops, and manage a peaceful virtual life.\n\nOn the other hand, visually stunning open-world adventures like Ghost of Tsushima immerse players in rich historical narratives and intense samurai combat. Even competitive sports titles like FC Online keep players engaged through strategic team building. This variety ensures that whether you want to relax or face a challenge, there is always a game for you.",
            level = "B1 Pre-Intermediate",
            category = "Entertainment",
            readTime = "5 min read",
            isCompleted = false,
            image = "https://images.unsplash.com/photo-1552820728-8b83bb6b773f?q=80&w=1000&auto=format&fit=crop"
        ),
        ArticleEntity(
            title = "Mastering Server-Side Rendering with Next.js",
            content = "Server-side rendering (SSR) has transformed how we build modern web applications. By utilizing Next.js alongside React, developers can significantly improve SEO and initial page load times. Understanding how to fetch data seamlessly on the server before rendering the UI is a critical skill for building fast, user-friendly platforms.",
            level = "C1 Advanced",
            category = "Technology",
            readTime = "6 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/tech1/800/600"
        ),
        ArticleEntity(
            title = "Building Robust APIs with Java Spring Boot",
            content = "Java Spring Boot remains a powerhouse for enterprise-level backend development. Its dependency injection and robust security features make it ideal for handling complex system requirement specifications. When combined with scalable databases, Spring Boot ensures high availability and reliable data processing for microservices.",
            level = "B2 Intermediate",
            category = "Technology",
            readTime = "7 min read",
            isCompleted = true,
            image = "https://picsum.photos/seed/code2/800/600"
        ),
        ArticleEntity(
            title = "PostgreSQL vs MySQL: Choosing Your Relational Database",
            content = "When designing the schema for a new project, choosing the right database is crucial. MySQL offers incredible speed for read-heavy operations and is highly popular. However, PostgreSQL provides advanced data types and strict adherence to SQL standards, making it the preferred choice for complex queries and data integrity.",
            level = "B2 Intermediate",
            category = "Technology",
            readTime = "5 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/db3/800/600"
        ),
        ArticleEntity(
            title = "The Psychology of Farming Simulators",
            content = "Farming simulators offer a unique therapeutic experience. By focusing on repetitive but rewarding tasks—like clearing land, planting crops, and interacting with townspeople—players find a sense of control and relaxation that is often missing in high-stakes competitive games. The daily routine becomes a comforting escape.",
            level = "B1 Pre-Intermediate",
            category = "Entertainment",
            readTime = "4 min read",
            isCompleted = true,
            image = "https://picsum.photos/seed/game4/800/600"
        ),
        ArticleEntity(
            title = "Historical Accuracy in Samurai Video Games",
            content = "Modern video games set in feudal Japan often blend historical facts with cinematic storytelling. Developers meticulously research architecture, weapon design, and traditional customs to create immersive open-world environments. Exploring these beautiful landscapes while engaging in tactical combat provides both education and entertainment.",
            level = "B2 Intermediate",
            category = "Entertainment",
            readTime = "6 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/samurai5/800/600"
        ),
        ArticleEntity(
            title = "Strategic Team Building in Football E-Sports",
            content = "Succeeding in competitive football titles requires more than just quick reflexes. Players must act as managers, carefully considering player statistics, formations, and tactical adjustments. Building a balanced squad with a limited budget is a challenge that mirrors real-world sports management.",
            level = "B1 Pre-Intermediate",
            category = "Entertainment",
            readTime = "5 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/sports6/800/600"
        ),
        ArticleEntity(
            title = "Understanding the Labor Theory of Value",
            content = "The labor theory of value argues that the economic value of a good or service is determined by the total amount of socially necessary labor required to produce it. This classical economic concept remains a fundamental pillar in political economy, sparking debates about surplus value, automation, and modern wage structures.",
            level = "C1 Advanced",
            category = "Economy",
            readTime = "8 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/econ7/800/600"
        ),
        ArticleEntity(
            title = "Dialectical Materialism: A Brief Introduction",
            content = "Dialectical materialism is a philosophical approach to reality derived from the teachings of Karl Marx and Friedrich Engels. It suggests that every economic order grows to a state of maximum efficiency, while simultaneously developing internal contradictions or weaknesses that contribute to its eventual decay.",
            level = "C1 Advanced",
            category = "Philosophy",
            readTime = "7 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/phil8/800/600"
        ),
        ArticleEntity(
            title = "Daily Habits for English Proficiency",
            content = "Consistency is the key to mastering a new language. Dedicating just 30 to 35 minutes a day to practicing pronunciation, listening to podcasts, or reviewing vocabulary can yield significant results. Regular practice is far more effective for standardized tests than cramming at the last minute.",
            level = "A2 Elementary",
            category = "Education",
            readTime = "4 min read",
            isCompleted = true,
            image = "https://picsum.photos/seed/study9/800/600"
        ),
        ArticleEntity(
            title = "Managing Dual-Boot Systems and SSD Partitions",
            content = "Setting up a dual-boot environment, such as running Ubuntu alongside Windows, requires careful SSD management. Hardware failures, like a drive disappearing from the BIOS, often point to partition table corruption or physical connection issues. Regular backups and understanding GRUB bootloader mechanics are essential.",
            level = "C1 Advanced",
            category = "Technology",
            readTime = "6 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/hardware10/800/600"
        ),
        ArticleEntity(
            title = "Optimizing Peripheral Devices for Productivity",
            content = "High-quality peripheral hardware, including mechanical keyboards and advanced mice with customizable macro configurations, can drastically improve your workflow. Adjusting DPI settings and programming shortcuts allows developers and gamers alike to reduce strain and increase efficiency during long sessions.",
            level = "B2 Intermediate",
            category = "Technology",
            readTime = "5 min read",
            isCompleted = true,
            image = "https://picsum.photos/seed/mouse11/800/600"
        ),
        ArticleEntity(
            title = "The Enduring Appeal of Die-Cast Toy Cars",
            content = "Collecting die-cast model cars is a hobby that spans generations. Enthusiasts hunt for rare editions, specific castings, and limited-run series. The thrill of finding a highly sought-after model in pristine condition keeps collectors scouring hobby shops and trading communities worldwide.",
            level = "B1 Pre-Intermediate",
            category = "Lifestyle",
            readTime = "4 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/car12/800/600"
        ),
        ArticleEntity(
            title = "Creating Impactful Dashboards with Data Visualization",
            content = "Transforming raw data into actionable insights requires effective visualization. Tools like Looker Studio allow analysts to build dynamic bubble charts, quadrant scatter plots, and consolidated reports. A well-designed dashboard highlights key metrics instantly, empowering management to make informed decisions.",
            level = "B2 Intermediate",
            category = "Business",
            readTime = "6 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/data13/800/600"
        ),
        ArticleEntity(
            title = "Designing Architecture for Modern HRMS",
            content = "A robust Human Resource Management System (HRMS) or Job Portal requires a detailed project charter and clear module definitions. From managing stakeholders to implementing precise job-matching algorithms, the system architecture must prioritize security, scalable databases, and an intuitive user interface.",
            level = "C1 Advanced",
            category = "Technology",
            readTime = "7 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/hrms14/800/600"
        ),
        ArticleEntity(
            title = "Key Features of Successful Health Tracking Apps",
            content = "The best fitness applications go beyond simply counting steps. They incorporate comprehensive workout journals, extensive exercise libraries, and personalized goal management. By tracking progress over time and providing visual feedback, these apps help users stay motivated and maintain a healthy lifestyle.",
            level = "B2 Intermediate",
            category = "Technology",
            readTime = "5 min read",
            isCompleted = true,
            image = "https://picsum.photos/seed/health15/800/600"
        ),
        ArticleEntity(
            title = "A Guide to Urban Parks and Green Spaces",
            content = "Urban parks play a vital role in city living, offering residents a place to relax away from the concrete. Knowing the best times to visit, understanding parking situations, and locating the most scenic gates can turn a simple afternoon stroll into a refreshing weekend retreat.",
            level = "A2 Elementary",
            category = "Travel",
            readTime = "3 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/park16/800/600"
        ),
        ArticleEntity(
            title = "The James Webb Space Telescope's Newest Discoveries",
            content = "Astronomers are continually amazed by the data transmitted by modern space telescopes. By observing infrared light, scientists can peer through cosmic dust to witness the birth of stars and the formation of early galaxies, rewriting our understanding of the universe's origins.",
            level = "B2 Intermediate",
            category = "Science",
            readTime = "5 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/space17/800/600"
        ),
        ArticleEntity(
            title = "Sustainable Supply Chains in 2026",
            content = "Global businesses are facing increasing pressure to adopt sustainable logistics. Companies are transitioning to electric fleets, optimizing warehouse operations to reduce waste, and demanding transparency from their suppliers. This shift is not just ethical; it's becoming an economic necessity.",
            level = "C1 Advanced",
            category = "Business",
            readTime = "6 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/biz18/800/600"
        ),
        ArticleEntity(
            title = "How AI is Reshaping Frontend Development",
            content = "Artificial intelligence is rapidly changing the frontend landscape. From AI-assisted code generation to smart UI components that adapt to user behavior, developers must learn to integrate these tools into their workflows. The future belongs to those who can bridge the gap between design systems and machine learning.",
            level = "B2 Intermediate",
            category = "Technology",
            readTime = "5 min read",
            isCompleted = false,
            image = "https://picsum.photos/seed/ai19/800/600"
        ),
        ArticleEntity(
            title = "Building a Minimalist Wardrobe with Neutral Tones",
            content = "A minimalist wardrobe focuses on versatility and comfort. By investing in staple pieces like charcoal t-shirts, navy sweaters, and high-quality khaki pants, you can easily mix and match outfits for any casual or athletic occasion. Neutral tones provide a clean, timeless aesthetic without the daily hassle of choosing what to wear.",
            level = "B1 Pre-Intermediate",
            category = "Lifestyle",
            readTime = "4 min read",
            isCompleted = true,
            image = "https://picsum.photos/seed/fashion20/800/600"
        )


        )

    // Ngữ pháp
    val grammarLessons = listOf(
        GrammarLessonEntity(
            id = 1L,
            title = "Danh từ (Nouns)",
            orderIndex = 1,
            theoryBasics = "Danh từ là từ dùng để chỉ người, vật, việc, địa điểm hoặc khái niệm trừu tượng. Được chia làm 2 loại chính: Danh từ đếm được (Countable Nouns) và Danh từ không đếm được (Uncountable Nouns).",
            usageRules = "1. Danh từ đếm được có thể ở dạng số ít (a cat) hoặc số nhiều (cats). Quy tắc chung là thêm 's' hoặc 'es' vào sau danh từ số ít.\n2. Danh từ không đếm được (water, milk, happiness) luôn đi với động từ số ít và không đi kèm với số đếm trực tiếp.",
            examples = "- Đếm được số ít: This is a book.\n- Đếm được số nhiều: There are three books on the table.\n- Không đếm được: Water is essential for human life."
        ),
        GrammarLessonEntity(
            id = 2L,
            title = "Động từ (Verbs)",
            orderIndex = 2,
            theoryBasics = "Động từ là những từ dùng để diễn tả hành động (run, eat) hoặc trạng thái (be, seem, feel) của chủ ngữ trong câu.",
            usageRules = "1. Động từ trạng thái (Stative Verbs) thường không chia ở các thì tiếp diễn (V-ing).\n2. Động từ hành động (Action Verbs) có thể chia ở tất cả các thì.\n3. Động từ phải luôn luôn chia phù hợp với ngôi và số của chủ ngữ.",
            examples = "- Diễn tả hành động: He runs 5 miles every morning.\n- Diễn tả trạng thái: I feel tired today.\n- Chia theo chủ ngữ: She eats apple / They eat apple."
        ),
        GrammarLessonEntity(
            id = 3L,
            title = "Tính từ (Adjectives)",
            orderIndex = 3,
            theoryBasics = "Tính từ là từ dùng để miêu tả đặc điểm, tính chất, màu sắc hoặc kích thước của một người, sự vật hoặc hiện tượng.",
            usageRules = "1. Tính từ thường đứng TRƯỚC danh từ để bổ nghĩa trực tiếp cho danh từ đó.\n2. Tính từ đứng SAU động từ liên kết (Linking Verbs) như to-be, seem, look, taste, feel.",
            examples = "- Đứng trước danh từ: She bought a beautiful dress.\n- Đứng sau động từ To-be: The sky is blue tonight.\n- Đứng sau động từ liên kết: This soup tastes delicious."
        ),
        GrammarLessonEntity(
            id = 4L,
            title = "Trạng từ (Adverbs)",
            orderIndex = 4,
            theoryBasics = "Trạng từ là từ dùng để bổ nghĩa cho động từ, tính từ hoặc cho một trạng từ khác, giúp làm rõ cách thức, mức độ, thời gian hoặc tần suất.",
            usageRules = "1. Trạng từ chỉ cách thức thường được thành lập bằng cách thêm đuôi '-ly' vào sau tính từ (quick -> quickly).\n2. Trạng từ chỉ cách thức thường đứng sau động từ thường hoặc đứng cuối câu.\n3. Trạng từ chỉ mức độ (very, extremely) đứng trước tính từ hoặc trạng từ mà nó bổ nghĩa.",
            examples = "- Bổ nghĩa động từ: He speaks English fluently.\n- Bổ nghĩa tính từ: She is an extremely smart student.\n- Trạng từ chỉ tần suất: They always go to school on time."
        ),
        GrammarLessonEntity(
            id = 5L,
            title = "Thì Hiện tại đơn (Present Simple)",
            orderIndex = 5,
            theoryBasics = "Cấu trúc khẳng định:\n- Với To-be: S + am/is/are + O\n- Với Động từ thường: S + V(s/es) + O",
            usageRules = "1. Diễn tả một sự thật hiển nhiên, một chân lý của tự nhiên.\n2. Diễn tả một thói quen, hành động lặp đi lặp lại có tính chu kỳ ở hiện tại.\n3. Thường đi kèm trạng từ tần suất: always, usually, often, everyday.",
            examples = "- Chân lý: The sun rises in the East.\n- Thói quen: I do exercise at 6 AM every day.\n- Phủ định: He does not like drinking coffee."
        ),
        GrammarLessonEntity(
            id = 6L,
            title = "Thì Quá khứ đơn (Past Simple)",
            orderIndex = 6,
            theoryBasics = "Cấu trúc khẳng định:\n- Với To-be: S + was/were + O\n- Với Động từ thường: S + V2/ed + O",
            usageRules = "1. Diễn tả một hành động đã xảy ra, kết thúc hoàn toàn trong quá khứ và biết rõ thời gian.\n2. Dấu hiệu nhận biết: yesterday, last night, last week, 2 years ago, in 2020.",
            examples = "- Động từ có quy tắc: We watched a great movie last night.\n- Động từ bất quy tắc: He went to Paris two years ago.\n- Câu hỏi: Did you see my car yesterday?"
        ),
        GrammarLessonEntity(
            id = 7L,
            title = "Thì Tương lai đơn (Simple Future)",
            orderIndex = 7,
            theoryBasics = "Cấu trúc khẳng định:\nS + will + V-inf (động từ nguyên thể) + O",
            usageRules = "1. Diễn tả một quyết định bộc phát ngay tại thời điểm nói chứ không có dự định trước.\n2. Diễn tả một dự đoán chủ quan, không có căn cứ thực tế rõ ràng ở hiện tại.\n3. Dấu hiệu nhận biết: tomorrow, next week, in the future, I think, I believe.",
            examples = "- Quyết định ngay lập tức: It's cold. I will close the window.\n- Dự đoán: I think Argentina will win the match.\n- Phủ định: They will not attend the meeting tomorrow."
        ),
        GrammarLessonEntity(
            id = 8L,
            title = "Câu đơn (Simple Sentences)",
            orderIndex = 8,
            theoryBasics = "Câu đơn là loại câu ngắn gọn nhất, chỉ bao gồm một mệnh đề độc lập duy nhất (One independent clause).",
            usageRules = "1. Bắt buộc phải có đầy đủ ít nhất một chủ ngữ (Subject) và một vị ngữ (Predicate).\n2. Diễn đạt một ý nghĩa hoàn chỉnh, trọn vẹn và độc lập, không phụ thuộc vào câu khác.",
            examples = "- Cấu trúc S-V-O cơ bản: The train was late.\n- Chủ ngữ ghép: Tom and Jerry are running.\n- Vị ngữ ghép: She sang and danced beautifully."
        ),
        GrammarLessonEntity(
            id = 9L,
            title = "Mệnh đề (Clauses)",
            orderIndex = 9,
            theoryBasics = "Mệnh đề là một tổ hợp từ chứa một cặp chủ ngữ và động từ chính đang được chia thì.",
            usageRules = "1. Mệnh đề độc lập (Independent clause): Có nghĩa hoàn chỉnh và tự đứng độc lập thành một câu đơn.\n2. Mệnh đề phụ thuộc (Dependent clause): Bắt đầu bằng liên từ phụ thuộc, không có ý nghĩa hoàn chỉnh nếu đứng một mình, phải gắn vào mệnh đề độc lập.",
            examples = "- Mệnh đề độc lập: We stayed inside.\n- Mệnh đề phụ thuộc: Because it was raining heavy.\n- Ghép hoàn chỉnh: Because it was raining heavy, we stayed inside."
        ),
        GrammarLessonEntity(
            id = 10L,
            title = "Câu phức (Complex Sentences)",
            orderIndex = 10,
            theoryBasics = "Câu phức là câu được cấu tạo bởi ít nhất một mệnh đề độc lập phối hợp với ít nhất một mệnh đề phụ thuộc.",
            usageRules = "1. Các mệnh đề được kết nối với nhau bằng liên từ phụ thuộc (because, although, if, since, when, while...).\n2. Nếu mệnh đề phụ thuộc đứng đầu câu, bắt buộc phải dùng dấu phẩy ',' để ngăn cách với mệnh đề độc lập.",
            examples = "- Mệnh đề phụ đứng sau: I will call you when I arrive at the station.\n- Mệnh đề phụ đứng đầu: Although he was tired, he finished his assignment."
        )
    )

    val grammarQuestion = listOf(
        // Questions for Lesson 1: Danh từ
        GrammarQuestionEntity(
            id = 101L,
            lessonId = 1L,
            questionText = "Từ nào sau đây là Danh từ không đếm được (Uncountable Noun)?",
            questionType = "MULTIPLE_CHOICE",
            options = "Apple, Milk, Book, Table",
            correctAnswer = "Milk"
        ),
        GrammarQuestionEntity(
            id = 102L,
            lessonId = 1L,
            questionText = "Điền dạng số nhiều của danh từ 'child': There are three ___ playing in the garden.",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "children"
        ),

        // Questions for Lesson 2: Động từ
        GrammarQuestionEntity(
            id = 201L,
            lessonId = 2L,
            questionText = "Xác định động từ chính trong câu sau: 'They always listen to music before bedtime.'",
            questionType = "MULTIPLE_CHOICE",
            options = "always, listen, music, bedtime",
            correctAnswer = "listen"
        ),
        GrammarQuestionEntity(
            id = 202L,
            lessonId = 2L,
            questionText = "Chia động từ 'be' phù hợp: She ___ a talented doctor at the local hospital.",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "is"
        ),

        // Questions for Lesson 3: Tính từ
        GrammarQuestionEntity(
            id = 301L,
            lessonId = 3L,
            questionText = "Chọn vị trí thích hợp cho tính từ: 'He bought a ___ vehicle yesterday.'",
            questionType = "MULTIPLE_CHOICE",
            options = "new, newly, news, newness",
            correctAnswer = "new"
        ),
        GrammarQuestionEntity(
            id = 302L,
            lessonId = 3L,
            questionText = "Điền tính từ miêu tả: 'This lemon juice tastes very ___ (chua).'",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "sour"
        ),

        // Questions for Lesson 4: Trạng từ
        GrammarQuestionEntity(
            id = 401L,
            lessonId = 4L,
            questionText = "Chọn trạng từ bổ nghĩa cho câu sau: 'She speaks English very ___.'",
            questionType = "MULTIPLE_CHOICE",
            options = "good, well, beautiful, fluent",
            correctAnswer = "well"
        ),
        GrammarQuestionEntity(
            id = 402L,
            lessonId = 4L,
            questionText = "Biến đổi tính từ 'bad' thành trạng từ để hoàn thành câu: He performed ___ in the interview.",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "badly"
        ),

        // Questions for Lesson 5: Hiện tại đơn
        GrammarQuestionEntity(
            id = 501L,
            lessonId = 5L,
            questionText = "Chọn đáp án đúng chia thì hiện tại đơn: 'The sun ___ in the West.'",
            questionType = "MULTIPLE_CHOICE",
            options = "set, sets, setting, is setting",
            correctAnswer = "sets"
        ),
        GrammarQuestionEntity(
            id = 502L,
            lessonId = 5L,
            questionText = "Điền trợ động từ phủ định phù hợp: We ___ (not like) watching horror movies.",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "do not like"
        ),

        // Questions for Lesson 6: Quá khứ đơn
        GrammarQuestionEntity(
            id = 601L,
            lessonId = 6L,
            questionText = "Chọn dạng quá khứ của động từ bất quy tắc 'go': 'We ___ to Paris last summer.'",
            questionType = "MULTIPLE_CHOICE",
            options = "go, goed, went, gone",
            correctAnswer = "went"
        ),
        GrammarQuestionEntity(
            id = 602L,
            lessonId = 6L,
            questionText = "Chia động từ trong ngoặc ở quá khứ: She ___ (visit) her grandmother two days ago.",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "visited"
        ),

        // Questions for Lesson 7: Tương lai đơn
        GrammarQuestionEntity(
            id = 701L,
            lessonId = 7L,
            questionText = "Chọn cấu trúc đúng diễn tả tương lai: 'I think it ___ rain tomorrow.'",
            questionType = "MULTIPLE_CHOICE",
            options = "is, will, would, going to",
            correctAnswer = "will"
        ),
        GrammarQuestionEntity(
            id = 702L,
            lessonId = 7L,
            questionText = "Điền dạng rút gọn của 'will not': I ___ forget what you told me.",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "won't"
        ),

        // Questions for Lesson 8: Câu đơn
        GrammarQuestionEntity(
            id = 801L,
            lessonId = 8L,
            questionText = "Câu nào sau đây là cấu trúc của câu đơn (Simple Sentence)?",
            questionType = "MULTIPLE_CHOICE",
            options = "The cat sat on the mat., I like tea but he likes coffee., If it rains we stay home., He failed because he was lazy.",
            correctAnswer = "The cat sat on the mat."
        ),
        GrammarQuestionEntity(
            id = 802L,
            lessonId = 8L,
            questionText = "Điền số lượng (bằng chữ tiếng Anh): Một câu đơn chứa tối đa bao nhiêu mệnh đề độc lập? Answer: ___",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "one"
        ),

        // Questions for Lesson 9: Mệnh đề
        GrammarQuestionEntity(
            id = 901L,
            lessonId = 9L,
            questionText = "Trong câu 'Although it was cold, we walked', mệnh đề 'Although it was cold' thuộc loại gì?",
            questionType = "MULTIPLE_CHOICE",
            options = "Independent clause, Dependent clause, Nounphrase, Verbphrase",
            correctAnswer = "Dependent clause"
        ),
        GrammarQuestionEntity(
            id = 902L,
            lessonId = 9L,
            questionText = "Mệnh đề có thể tự đứng độc lập tạo thành một câu có nghĩa gọi là mệnh đề ___ (tiếng Việt).",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "độc lập"
        ),

        // Questions for Lesson 10: Câu phức
        GrammarQuestionEntity(
            id = 1001L,
            lessonId = 10L,
            questionText = "Chọn liên từ phù hợp tạo câu phức chỉ sự tương phản: '___ he was wealthy, he lived very savingly.'",
            questionType = "MULTIPLE_CHOICE",
            options = "Because, Although, And, But",
            correctAnswer = "Although"
        ),
        GrammarQuestionEntity(
            id = 1002L,
            lessonId = 10L,
            questionText = "Sử dụng liên từ nối lý do để hoàn thành câu phức: We didn't go out ___ the weather was terrible.",
            questionType = "FILL_IN_BLANK",
            options = null,
            correctAnswer = "because"
        )
    )

    val stories = listOf(
        StoryEntity(
            id = 10001,
            title = "HARRY POTTER THE COMPLETE COLLECTION",
            content = "Nearly ten years had passed since the Dursleys had woken up to find\n" +
                    "their nephew on the front step, but Privet Drive had hardly changed at\n" +
                    "all. The sun rose on the same tidy front gardens and lit up the brass number\n" +
                    "four on the Dursleys’ front door; it crept into their living room, which was\n" +
                    "almost exactly the same as it had been on the night when Mr. Dursley had\n" +
                    "seen that fateful news report about the owls. Only the photographs on the\n" +
                    "mantelpiece really showed how much time had passed. Ten years ago, there\n" +
                    "had been lots of pictures of what looked like a large pink beach ball wearing\n" +
                    "different-colored bonnets — but Dudley Dursley was no longer a baby, and\n" +
                    "now the photographs showed a large blond boy riding his first bicycle, on a\n" +
                    "carousel at the fair, playing a computer game with his father, being hugged\n" +
                    "and kissed by his mother. The room held no sign at all that another boy lived\n" +
                    "in the house, too.\n" +
                    "Yet Harry Potter was still there, asleep at the moment, but not for long. His\n" +
                    "Aunt Petunia was awake and it was her shrill voice that made the first noise of\n" +
                    "the day.\n" +
                    "“Up! Get up! Now!”\n" +
                    "Harry woke with a start. His aunt rapped on the door again.\n" +
                    "“Up!” she screeched. Harry heard her walking toward the kitchen and then\n" +
                    "the sound of the frying pan being put on the stove. He rolled onto his back\n" +
                    "and tried to remember the dream he had been having. It had been a good one.\n" +
                    "There had been a flying motorcycle in it. He had a funny feeling he’d had the\n" +
                    "same dream before.\n" +
                    "His aunt was back outside the door.\n" +
                    "“Are you up yet?” she demanded.\n" +
                    "“Nearly,” said Harry.\n" +
                    "“Well, get a move on, I want you to look after the bacon. And don’t you\n" +
                    "dare let it burn, I want everything perfect on Duddy’s birthday.”\n" +
                    "Harry groaned.\n" +
                    "“What did you say?” his aunt snapped through the door.\n" +
                    "“Nothing, nothing . . .”\n" +
                    "Dudley’s birthday — how could he have forgotten? Harry got slowly out of\n" +
                    "bed and started looking for socks. He found a pair under his bed and, after\n" +
                    "pulling a spider off one of them, put them on. Harry was used to spiders,\n" +
                    "because the cupboard under the stairs was full of them, and that was where he\n" +
                    "slept.\n",
            category = "Fantasy",
            level = "c1",
            image = "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=500",
            author = "JK.ROWLING",
        ),
        StoryEntity(
            id = 10002,
            title = "THE LORD OF THE RINGS: THE FELLOWSHIP OF THE RING",
            content = "When Mr. Bilbo Baggins of Bag End announced that he would shortly be\n" +
                    "celebrating his eleventy-first birthday with a party of special magnificence,\n" +
                    "there was much talk and excitement in Hobbiton. Bilbo was very rich and very\n" +
                    "peculiar, and had been the wonder of the Hobbiton for sixty years, ever since\n" +
                    "his remarkable disappearance and unexpected return. The riches he had brought\n" +
                    "back from his travels had now become a local legend, and it was popularly\n" +
                    "believed, whatever the old folk might say, that the Hill at Bag End was full\n" +
                    "of tunnels stuffed with treasure.\n" +
                    "And if that was not enough for fame, there was also his prolonged vigor to\n" +
                    "marvel at. Time wore on, but it seemed to have little effect on Mr. Baggins.\n" +
                    "At ninety he was much the same as at fifty. At ninety-nine they began to\n" +
                    "call him well-preserved, but unchanged would have been nearer the mark.\n" +
                    "There were some that shook their heads and thought this was too much of a\n" +
                    "good thing; it seemed unfair that anyone should possess apparently\n" +
                    "perpetual youth as well as inexhaustible wealth.\n" +
                    "\"It will have to be paid for,\" they said. \"It isn't natural, and trouble\n" +
                    "will come of it!\"\n" +
                    "But so far trouble had not come; and as Mr. Baggins was generous with his\n" +
                    "money, most people were willing to forgive him his oddities and his good\n" +
                    "fortune. He remained on visiting terms with his relatives, and he had many\n" +
                    "devoted admirers among the hobbits of poor and unimportant families.\n",
            category = "Fantasy",
            level = "b2",
            image = "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0?w=500",
            author = "J.R.R. TOLKIEN",
        ),

        StoryEntity(
            id = 10003,
            title = "THE GREAT GATSBY",
            content = "In my younger and more vulnerable years my father gave me some advice\n" +
                    "that I've been turning over in my mind ever since.\n" +
                    "\"Whenever you feel like criticizing anyone,\" he told me, \"just remember\n" +
                    "that all the people in this world haven't had the advantages that you've had.\"\n" +
                    "He didn't say any more, but we've always been unusually communicative in\n" +
                    "a reserved way, and I understood that he meant a great deal more than that.\n" +
                    "In consequence, I'm inclined to reserve all judgments, a habit that has\n" +
                    "opened up many curious natures to me and also made me the victim of not a\n" +
                    "few veteran bores. The abnormal mind is quick to detect and attach itself\n" +
                    "to this quality when it appears in a normal person, and so it came about\n" +
                    "that in college I was unjustly accused of being a politician, because I was\n" +
                    "privy to the secret griefs of wild, unknown men. Most of the confidences\n" +
                    "were unsought — frequently I have feigned sleep, preoccupation, or a\n" +
                    "hostile levity when I realized by some unmistakable sign that an intimate\n" +
                    "revelation was quivering on the horizon; for the intimate revelations of\n" +
                    "young men, or at least the terms in which they express them, are usually\n" +
                    "plagiaristic and marred by obvious suppressions.\n" +
                    "Reserving judgments is a matter of infinite hope. I am still a little\n" +
                    "afraid of missing something if I forget that, as my father snobbishly\n" +
                    "suggested, and I snobbishly repeat, a sense of the fundamental decencies\n" +
                    "is parcelled out unequally at birth.\n",
            category = "Classic",
            level = "c1",
            image = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500",
            author = "F. SCOTT FITZGERALD",
        ),

        StoryEntity(
            id = 10004,
            title = "PRIDE AND PREJUDICE",
            content = "It is a truth universally acknowledged, that a single man in possession\n" +
                    "of a good fortune, must be in want of a wife.\n" +
                    "However little known the feelings or views of such a man may be on his\n" +
                    "first entering a neighbourhood, this truth is so well fixed in the minds\n" +
                    "of the surrounding families, that he is considered as the rightful property\n" +
                    "of some one or other of their daughters.\n" +
                    "\"My dear Mr. Bennet,\" said his lady to him one day, \"have you heard that\n" +
                    "Netherfield Park is let at last?\"\n" +
                    "Mr. Bennet replied that he had not.\n" +
                    "\"But it is,\" returned she; \"for Mrs. Long has just been here, and she\n" +
                    "told me all about it.\"\n" +
                    "Mr. Bennet made no answer.\n" +
                    "\"Do you not want to know who has taken it?\" cried his wife impatiently.\n" +
                    "\"You want to tell me, and I have no objection to hearing it.\"\n" +
                    "This was invitation enough.\n" +
                    "\"Why, my dear, you must know, Mrs. Long says that Netherfield is taken\n" +
                    "by a young man of large fortune from the north of England; that he came\n" +
                    "down on Monday in a chaise and four to see the place, and was so much\n" +
                    "delighted with it, that he agreed with Mr. Morris immediately; that he is\n" +
                    "to take possession before Michaelmas, and some of his servants are to be\n" +
                    "in the house by the end of next week.\"\n" +
                    "\"What is his name?\"\n" +
                    "\"Bingley.\"\n" +
                    "\"Is he married or single?\"\n" +
                    "\"Oh! Single, my dear, to be sure! A single man of large fortune; four or\n" +
                    "five thousand a year. What a fine thing for our girls!\"\n",
            category = "Romance",
            level = "b2",
            image = "https://images.unsplash.com/photo-1474932430478-367dbb6832c1?w=500",
            author = "JANE AUSTEN",
        ),
        StoryEntity(
            id = 10005,
            title = "THE ALCHEMIST",
            content = "The boy's name was Santiago. Dusk was falling as the boy arrived with\n" +
                    "his herd at an abandoned church. The roof had fallen in long ago, and an\n" +
                    "enormous sycamore had grown up where the sacristy had once stood.\n" +
                    "He decided to spend the night there. He saw to it that all the sheep\n" +
                    "entered through the ruined gate, and then laid down his jacket and\n" +
                    "used it for a pillow. He told himself that he would have to begin\n" +
                    "reading thicker books: they lasted longer, and made more comfortable\n" +
                    "pillows.\n" +
                    "It was still dark when he awoke, and, looking up, he could see the\n" +
                    "stars through the half-destroyed roof. I wanted to sleep a little\n" +
                    "longer, he thought. He had the same dream that night as a week ago,\n" +
                    "and once again he had awakened before it ended.\n" +
                    "He arose and, taking up his crook, began to awaken the sheep that\n" +
                    "still slept. He had noticed that, as soon as he awoke, most of his\n" +
                    "animals also began to stir. It was as if some mysterious energy\n" +
                    "bound his life to that of the sheep, with whom he had spent the\n" +
                    "past two years, leading them through the countryside of Andalusia\n" +
                    "in search of food and water.\n" +
                    "\"They are so used to me that they know my schedule,\" he muttered.\n" +
                    "Thinking about that for a moment, he realized that it could be the\n" +
                    "other way around: that it was he who had become accustomed to\n" +
                    "their schedule.\n" +
                    "There were some people, though, who felt envious of his way of\n" +
                    "life and said that shepherds never need comb their hair, and have\n" +
                    "no fixed address.\n",
            category = "Adventure",
            level = "b1",
            image = "https://images.unsplash.com/photo-1509316785289-025f5b846b35?w=500",
            author = "PAULO COELHO",
        ),

        StoryEntity(
            id = 10006,
            title = "1984",
            content = "It was a bright cold day in April, and the clocks were striking thirteen.\n" +
                    "Winston Smith, his chin nuzzled into his breast in an effort to escape\n" +
                    "the vile wind, slipped quickly through the glass doors of Victory\n" +
                    "Mansions, though not quickly enough to prevent a swirl of gritty dust\n" +
                    "from entering along with him.\n" +
                    "The hallway smelt of boiled cabbage and old rag mats. At one end of\n" +
                    "it a coloured poster, too large for the room, had been tacked to the\n" +
                    "wall. It depicted simply an enormous face, more than a metre wide:\n" +
                    "the face of a man of about forty-five, with a heavy black moustache\n" +
                    "and ruggedly handsome features.\n" +
                    "Winston made for the stairs. It was no use trying the lift. Even at\n" +
                    "the best of times it was seldom working, and at present the electric\n" +
                    "current was cut off during daylight hours. It was part of the economy\n" +
                    "drive in preparation for Hate Week. The flat was seven flights up,\n" +
                    "and Winston, who was thirty-nine and had a varicose ulcer above his\n" +
                    "right ankle, went slowly, resting several times on the way.\n" +
                    "On each landing, opposite the lift shaft, the poster with the enormous\n" +
                    "face gazed from the wall. It was one of those pictures which are so\n" +
                    "contrived that the eyes follow you about when you move. BIG BROTHER\n" +
                    "IS WATCHING YOU, the caption beneath it ran.\n" +
                    "\"Down with Big Brother!\" he said to himself. But it was no use.\n" +
                    "The words had been forming in his mind for weeks now. He did not\n" +
                    "know why they had suddenly crystallized into an overt act.\n",
            category = "Dystopia",
            level = "c1",
            image = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500",
            author = "GEORGE ORWELL",
        ),

        StoryEntity(
            id = 10007,
            title = "THE LITTLE PRINCE",
            content = "Once when I was six years old I saw a magnificent picture in a book,\n" +
                    "called True Stories from Nature, about the primeval forest. It was a\n" +
                    "picture of a boa constrictor in the act of swallowing an animal. Here\n" +
                    "is a copy of the drawing.\n" +
                    "In the book it said: Boa constrictors swallow their prey whole, without\n" +
                    "chewing it. After that they are not able to move, and they sleep through\n" +
                    "the six months that they need for digestion.\n" +
                    "I pondered deeply, then, over the adventures of the jungle. And after\n" +
                    "some work with a colored pencil I succeeded in making my first drawing.\n" +
                    "My Drawing Number One. It looked something like this.\n" +
                    "I showed my masterpiece to the grown-ups, and asked them whether the\n" +
                    "drawing frightened them. But they answered: Frighten? Why should any\n" +
                    "one be frightened by a hat?\n" +
                    "My drawing was not a picture of a hat. It was a picture of a boa\n" +
                    "constrictor digesting an elephant. But since the grown-ups were not\n" +
                    "able to understand it, I made another drawing: I drew the inside of a\n" +
                    "boa constrictor, so that the grown-ups could see it clearly. They always\n" +
                    "need to have things explained.\n" +
                    "The grown-ups' response, this time, was to advise me to lay aside my\n" +
                    "drawings of boa constrictors, whether from the inside or the outside,\n" +
                    "and devote myself instead to geography, history, arithmetic, and grammar.\n" +
                    "That is why, at the age of six, I gave up what might have been a\n" +
                    "magnificent career as a painter.\n",
            category = "Fiction",
            level = "a2",
            image = "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?w=500",
            author = "ANTOINE DE SAINT-EXUPÉRY",
        ),
        StoryEntity(
            id = 10008,
            title = "TO KILL A MOCKINGBIRD",
            content = "When he was nearly thirteen, my brother Jem got his arm badly broken\n" +
                    "at the elbow. When it healed, and Jem's fears of never being able to\n" +
                    "play football were assuaged, he was seldom self-conscious about his\n" +
                    "injury. His left arm was somewhat shorter than his right; when he\n" +
                    "stood or walked, the back of his hand was at right angles to his body,\n" +
                    "his thumb parallel to his thigh.\n" +
                    "When enough years had gone by to enable us to look back on them, we\n" +
                    "sometimes discussed the events leading to his accident. I maintain that\n" +
                    "the Ewells started it all, but Jem, who was four years my senior, said\n" +
                    "it started long before that. He said it began the summer Dill came to us,\n" +
                    "when Dill first gave us the idea of making Boo Radley come out.\n" +
                    "I said if he wanted to take a broad view of the thing, it really began\n" +
                    "with Andrew Jackson. If General Jackson hadn't run the Creeks up the\n" +
                    "creek, Simon Finch would never have paddled up the Alabama, and where\n" +
                    "would we be if he hadn't? We were far too old to settle an argument\n" +
                    "with a fistfight, so we consulted Atticus. Our father said we were\n" +
                    "both right.\n" +
                    "Being Southerners, it was a source of shame to some members of the\n" +
                    "family that we had no recorded ancestors on either side of the Battle\n" +
                    "of Hastings. All we had was Simon Finch, a fur-trapping apothecary\n" +
                    "from Cornwall whose piety was exceeded only by his greed.\n" +
                    "Atticus told us to delete the adjectives and we'd have the facts.\n",
            category = "Classic",
            level = "b2",
            image = "https://images.unsplash.com/photo-1497864149936-d3163f0c0f4b?w=500",
            author = "HARPER LEE",
        ),

        StoryEntity(
            id = 10009,
            title = "BRAVE NEW WORLD",
            content = "A squat grey building of only thirty-four stories. Over the main\n" +
                    "entrance the words, CENTRAL LONDON HATCHERY AND CONDITIONING CENTRE,\n" +
                    "and, in a shield, the World State's motto, COMMUNITY, IDENTITY, STABILITY.\n" +
                    "The enormous room on the ground floor faced towards the north. Cold\n" +
                    "for all the summer beyond the panes, for all the tropical heat of the\n" +
                    "room itself, a harsh thin light glared through the windows, hungrily\n" +
                    "seeking some draped lay figure, some pallid shape of academic goose-flesh,\n" +
                    "but finding only the glass and nickel and bleakly shining porcelain of\n" +
                    "a laboratory.\n" +
                    "Wintriness responded to wintriness. The overalls of the workers were\n" +
                    "white, their hands gloved with a pale corpse-coloured rubber. The light\n" +
                    "was frozen, dead, a ghost. Only from the yellow barrels of the\n" +
                    "microscopes did it borrow a certain rich and living substance, lying\n" +
                    "along the polished tubes like butter, streak after luscious streak in\n" +
                    "long recession down the work tables.\n" +
                    "\"And this,\" said the Director opening the door, \"is the Fertilizing Room.\"\n" +
                    "Bent over their instruments, three hundred Fertilizers were plunged,\n" +
                    "as the Director of Hatcheries and Conditioning entered the room, in the\n" +
                    "scarcely breathing silence, the absent-minded, soliloquizing hum or\n" +
                    "whistle, of absorbed concentration.\n" +
                    "A troop of newly arrived students, very young, pink and callow, followed\n" +
                    "nervously, rather abjectly, at the Director's heels. Each of them carried\n" +
                    "a notebook, in which, whenever the great man spoke, he desperately\n" +
                    "scribbled.\n",
            category = "Dystopia",
            level = "c1",
            image = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=500",
            author = "ALDOUS HUXLEY",
        ),

        StoryEntity(
            id = 10010,
            title = "SHERLOCK HOLMES: A STUDY IN SCARLET",
            content = "In the year 1878 I took my degree of Doctor of Medicine of the\n" +
                    "University of London, and proceeded to Netley to go through the course\n" +
                    "prescribed for surgeons in the army. Having completed my studies there,\n" +
                    "I was duly attached to the Fifth Northumberland Fusiliers as Assistant\n" +
                    "Surgeon. The regiment was stationed in India at the time, and before\n" +
                    "I could join it, the second Afghan war had broken out.\n" +
                    "On landing at Bombay, I learned that my corps had advanced through\n" +
                    "the passes, and was already deep in the enemy's country. I followed,\n" +
                    "however, with many other officers who were in the same situation as\n" +
                    "myself, and succeeded in reaching Candahar in safety, where I found\n" +
                    "my regiment, and at once entered upon my new duties.\n" +
                    "The campaign brought honours and promotion to many, but for me it\n" +
                    "had nothing but misfortune and disaster. I was removed from my brigade\n" +
                    "and attached to the Berkshires, with whom I served at the fatal battle\n" +
                    "of Maiwand. There I was struck on the shoulder by a Jezail bullet,\n" +
                    "which shattered the bone and grazed the subclavian artery.\n" +
                    "I should have fallen into the hands of the murderous Ghazis had it\n" +
                    "not been for the devotion and courage shown by Murray, my orderly,\n" +
                    "who threw me across a pack-horse, and succeeded in bringing me safely\n" +
                    "to the British lines.\n" +
                    "\"You have been in Afghanistan, I perceive,\" said Sherlock Holmes.\n",
            category = "Mystery",
            level = "b2",
            image = "https://images.unsplash.com/photo-1587393855524-087f83d95bc9?w=500",
            author = "ARTHUR CONAN DOYLE",
        ),

        StoryEntity(
            id = 10011,
            title = "THE HUNGER GAMES",
            content = "When I wake up, the other side of the bed is cold. My fingers stretch\n" +
                    "out, seeking Prim's warmth but finding only the rough canvas cover of\n" +
                    "the mattress. She must have had bad dreams and climbed in with our\n" +
                    "mother. Of course she did. This is the morning of the reaping.\n" +
                    "I prop myself up on one elbow. There's enough light in the bedroom\n" +
                    "to see them. My little sister, Prim, curled up on her side, cocooned\n" +
                    "in my mother's body, their cheeks pressed together. In sleep my mother\n" +
                    "looks young and beautiful as she did in the photographs before the\n" +
                    "dark days, before my father's death.\n" +
                    "I quietly get up and make sure there's enough blanket to go around.\n" +
                    "Sitting on the floor, I pull on my hunting boots. Supple leather that\n" +
                    "has molded to my feet, they were my father's and I've worn them for\n" +
                    "several years now. I lace them up and look around the room, which\n" +
                    "holds the three of us and our few possessions.\n" +
                    "Our part of District 12, nicknamed the Seam, is usually crawling\n" +
                    "with coal miners heading out to the morning shift at this hour. But\n" +
                    "today the black cinder streets are empty. Reaping Day is a holiday.\n" +
                    "A deeply feared holiday. No work in the mines. No school.\n" +
                    "Instead, at two o'clock, we will all converge in the square for the\n" +
                    "reaping. All the residents of District 12. We stand in roped areas\n" +
                    "marked off by age. Behind the rope in the very front, the boys and\n" +
                    "girls between twelve and eighteen.\n",
            category = "Adventure",
            level = "b1",
            image = "https://images.unsplash.com/photo-1504701954957-2010ec3bcec1?w=500",
            author = "SUZANNE COLLINS",
        ),

        StoryEntity(
            id = 10012,
            title = "JANE EYRE",
            content = "There was no possibility of taking a walk that day. We had been\n" +
                    "wandering, indeed, in the leafless shrubbery an hour in the morning;\n" +
                    "but since dinner (Mrs. Reed, when there was no company, dined early)\n" +
                    "the cold winter wind had brought with it clouds so sombre, and a rain\n" +
                    "so penetrating, that further outdoor exercise was now out of the question.\n" +
                    "I was glad of it: I never liked long walks, especially on chilly\n" +
                    "afternoons: dreadful to me was the coming home in the raw twilight,\n" +
                    "with nipped fingers and toes, and a heart saddened by the chidings\n" +
                    "of Bessie, the nurse, and humbled by the consciousness of my physical\n" +
                    "inferiority to Eliza, John, and Georgiana Reed.\n" +
                    "The said Eliza, John, and Georgiana were now clustered round their\n" +
                    "mama in the drawing-room: she lay reclined on a sofa by the fireside,\n" +
                    "and with her darlings about her (for the time neither quarrelling nor\n" +
                    "crying) looked perfectly happy. Me, she had dispensed from joining\n" +
                    "the group; saying, \"She regretted to be under the necessity of keeping\n" +
                    "me at a distance; but that until she heard from Bessie, and could\n" +
                    "discover by her own observation, that I was endeavouring in good\n" +
                    "earnest to acquire a more sociable and childlike disposition, a more\n" +
                    "attractive and sprightly manner — something lighter, franker, more\n" +
                    "natural, as it were — she really must exclude me from privileges\n" +
                    "intended only for contented, happy little children.\"\n",
            category = "Romance",
            level = "c1",
            image = "https://images.unsplash.com/photo-1455390582262-044cdead277a?w=500",
            author = "CHARLOTTE BRONTË",
        )

    )



}