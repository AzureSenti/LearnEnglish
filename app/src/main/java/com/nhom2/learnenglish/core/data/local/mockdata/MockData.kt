package com.nhom2.learnenglish.core.data.local.mockdata

import com.nhom2.learnenglish.core.data.local.entity.ArticleEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarLessonEntity
import com.nhom2.learnenglish.core.data.local.entity.grammar.GrammarQuestionEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordEntity
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetCrossRef
import com.nhom2.learnenglish.core.data.local.entity.word.WordSetEntity

object MockData {

    // Từ vựng
    val wordSets = listOf(
        WordSetEntity(id = 1, name = "Giao tiếp cơ bản", description = "Các câu chào hỏi và từ vựng thông dụng hàng ngày.", unlockCost = 0),
        WordSetEntity(id = 2, name = "Công nghệ thông tin", description = "Từ vựng chuyên ngành lập trình và phần cứng.", unlockCost = 100),
        WordSetEntity(id = 3, name = "Kinh doanh & Tài chính", description = "Từ vựng về hợp đồng, đầu tư và đàm phán.", unlockCost = 200),
        WordSetEntity(id = 4, name = "Du lịch & Khám phá", description = "Hành lý, sân bay và các địa điểm tham quan.", unlockCost = 50)
    )

    val words = listOf(
        WordEntity(id = 1, englishWord = "Hello", vietnameseMeaning = "Xin chào", audio = "hello_audio_url"),
        WordEntity(id = 2, englishWord = "Thank you", vietnameseMeaning = "Cảm ơn", audio = "thanks_audio_url"),
        WordEntity(id = 3, englishWord = "Please", vietnameseMeaning = "Vui lòng / Làm ơn", audio = "please_audio_url"),
        WordEntity(id = 4, englishWord = "Sorry", vietnameseMeaning = "Xin lỗi", audio = "sorry_audio_url"),
        WordEntity(id = 5, englishWord = "Goodbye", vietnameseMeaning = "Tạm biệt", audio = "goodbye_audio_url"),

        WordEntity(id = 6, englishWord = "Database", vietnameseMeaning = "Cơ sở dữ liệu", audio = "db_audio_url"),
        WordEntity(id = 7, englishWord = "Algorithm", vietnameseMeaning = "Thuật toán", audio = "algo_audio_url"),
        WordEntity(id = 8, englishWord = "Application", vietnameseMeaning = "Ứng dụng", audio = "app_audio_url"),
        WordEntity(id = 9, englishWord = "Variable", vietnameseMeaning = "Biến số", audio = "var_audio_url"),
        WordEntity(id = 10, englishWord = "Server", vietnameseMeaning = "Máy chủ", audio = "server_audio_url"),

        WordEntity(id = 11, englishWord = "Contract", vietnameseMeaning = "Hợp đồng", audio = "contract_audio_url"),
        WordEntity(id = 12, englishWord = "Investment", vietnameseMeaning = "Đầu tư", audio = "invest_audio_url"),
        WordEntity(id = 13, englishWord = "Negotiation", vietnameseMeaning = "Đàm phán", audio = "nego_audio_url"),
        WordEntity(id = 14, englishWord = "Profit", vietnameseMeaning = "Lợi nhuận", audio = "profit_audio_url"),
        WordEntity(id = 15, englishWord = "Bankruptcy", vietnameseMeaning = "Phá sản", audio = "bankrupt_audio_url")
    )

    val wordSetRefs = listOf(
        WordSetCrossRef(1, 1), WordSetCrossRef(2, 1), WordSetCrossRef(3, 1), WordSetCrossRef(4, 1), WordSetCrossRef(5, 1),
        WordSetCrossRef(6, 2), WordSetCrossRef(7, 2), WordSetCrossRef(8, 2), WordSetCrossRef(9, 2), WordSetCrossRef(10, 2),
        WordSetCrossRef(11, 3), WordSetCrossRef(12, 3), WordSetCrossRef(13, 3), WordSetCrossRef(14, 3), WordSetCrossRef(15, 3)
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
            isCompleted = true
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
            isCompleted = false
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



}