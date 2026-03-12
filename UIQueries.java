public class UIQueries {

    private static final String CSS = 
        ":root {" +
        "  --primary: #2c3e50;" +
        "  --accent: #3498db;" +
        "  --bg: #f5f6fa;" +
        "  --glass: rgba(255, 255, 255, 0.7);" +
        "  --text: #2f3640;" +
        "  --sidebar-w: 260px;" +
        "}" +
        "* { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }" +
        "body { background: var(--bg); color: var(--text); overflow-x: hidden; }" +
        ".glass-card { background: var(--glass); backdrop-filter: blur(10px); border-radius: 15px; border: 1px solid rgba(255,255,255,0.3); box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37); }" +
        
        "/* Login Page */" +
        ".login-container { height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #1e3799, #0984e3); }" +
        ".login-card { padding: 40px; width: 380px; text-align: center; }" +
        ".login-card h1 { color: #fff; margin-bottom: 30px; font-weight: 300; letter-spacing: 2px; }" +
        ".login-card input { width: 100%; padding: 12px; margin-bottom: 20px; border: none; border-radius: 8px; background: rgba(255,255,255,0.2); color: #fff; outline: none; }" +
        ".login-card input::placeholder { color: rgba(255,255,255,0.7); }" +
        ".login-card button { width: 100%; padding: 12px; border: none; border-radius: 8px; background: #fff; color: #1e3799; font-weight: bold; cursor: pointer; transition: 0.3s; }" +
        ".login-card button:hover { background: #0984e3; color: #fff; }" +

        "/* Dashboard */" +
        ".dashboard { display: flex; height: 100vh; }" +
        ".sidebar { width: var(--sidebar-w); background: var(--primary); color: #fff; padding: 30px 20px; display: flex; flex-direction: column; }" +
        ".sidebar h2 { margin-bottom: 40px; font-weight: 300; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 15px; }" +
        ".nav-item { padding: 15px; border-radius: 8px; margin-bottom: 10px; cursor: pointer; transition: 0.3s; color: rgba(255,255,255,0.7); text-decoration: none; display: block; }" +
        ".nav-item:hover, .nav-item.active { background: var(--accent); color: #fff; }" +
        ".main-content { flex: 1; padding: 40px; overflow-y: auto; }" +
        ".header { margin-bottom: 40px; display: flex; justify-content: space-between; align-items: center; }" +
        ".card { background: #fff; padding: 25px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); margin-bottom: 30px; }" +
        
        "/* Table & Forms */" +
        "table { width: 100%; border-collapse: collapse; margin-top: 20px; }" +
        "th, td { text-align: left; padding: 15px; border-bottom: 1px solid #eee; }" +
        "th { background: #f8f9fa; color: #777; font-weight: 600; text-transform: uppercase; font-size: 12px; }" +
        "input, select { padding: 10px; border: 1px solid #ddd; border-radius: 6px; margin: 5px; }" +
        ".btn-primary { background: var(--accent); color: #fff; padding: 10px 20px; border: none; border-radius: 6px; cursor: pointer; }" +
        ".btn-edit { background: #f39c12; color: #fff; padding: 5px 10px; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }";

    public static String getLoginPage() {
        return "<html><head><style>" + CSS + "</style></head><body>" +
               "<div class='login-container'>" +
               "  <div class='login-card glass-card'>" +
               "    <h1>LIBRARY LOGIN</h1>" +
               "    <form action='/login' method='POST'>" +
               "      <input type='text' name='username' placeholder='Username' required>" +
               "      <input type='password' name='password' placeholder='Password' required>" +
               "      <button type='submit'>LOGIN</button>" +
               "    </form>" +
               "  </div>" +
               "</div></body></html>";
    }

    public static String getDashboardPage() {
        return "<html><head><style>" + CSS + "</style></head><body>" +
               "<div class='dashboard'>" +
               "  <div class='sidebar'>" +
               "    <h2>Digital Library</h2>" +
               "    <a onclick='showSection(\"overview\")' class='nav-item active' id='nav-overview'>Dashboard</a>" +
               "    <a onclick='showSection(\"books\")' class='nav-item' id='nav-books'>Books Inventory</a>" +
               "    <a onclick='showSection(\"members\")' class='nav-item' id='nav-members'>Members List</a>" +
               "    <a onclick='showSection(\"issue\")' class='nav-item' id='nav-issue'>Issue/Return</a>" +
               "    <div style='margin-top:auto'><a href='/' class='nav-item'>Logout</a></div>" +
               "  </div>" +
               "  <div class='main-content'>" +
               "    <div id='overview-section' class='section'>" +
               "      <div class='header'><h1>Dashboard Overview</h1></div>" +
               "      <div class='card'><h3>Welcome, Admin!</h3><p>Manage your books and members with ease.</p></div>" +
               "    </div>" +
               "    <div id='books-section' class='section' style='display:none'>" +
               "      <div class='header'><h1>Books Inventory</h1><button class='btn-primary' onclick='openModal(\"bookModal\")'>+ Add Book</button></div>" +
               "      <div class='card'><table id='bookTable'><thead><tr><th>ID</th><th>Title</th><th>Author</th><th>Quantity</th><th>Actions</th></tr></thead><tbody></tbody></table></div>" +
               "    </div>" +
               "    <div id='members-section' class='section' style='display:none'>" +
               "      <div class='header'><h1>Members List</h1><button class='btn-primary' onclick='openModal(\"memberModal\")'>+ Add Member</button></div>" +
               "      <div class='card'><table id='memberTable'><thead><tr><th>ID</th><th>Name</th><th>Phone</th><th>Actions</th></tr></thead><tbody></tbody></table></div>" +
               "    </div>" +
               "    <div id='issue-section' class='section' style='display:none'>" +
               "      <div class='header'><h1>Issue & Return</h1></div>" +
               "      <div class='card'><h3>Issue Book</h3><br>" +
               "        <input type='number' id='issueBid' placeholder='Book ID'> " +
               "        <input type='number' id='issueMid' placeholder='Member ID'> " +
               "        <button class='btn-primary' onclick='issueBook()'>Issue</button>" +
               "      </div>" +
               "      <div class='card'><h3>Return Book</h3><br>" +
               "        <input type='number' id='returnBid' placeholder='Book ID'> " +
               "        <input type='number' id='returnMid' placeholder='Member ID'> " +
               "        <button class='btn-primary' onclick='returnBook()'>Return</button>" +
               "      </div>" +
               "    </div>" +
               "  </div>" +
               "</div>" +
               "<!-- Modals -->" +
               "<div id='bookModal' style='display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); align-items:center; justify-content:center;'>" +
               "  <div class='card' style='width:400px'><h2 id='bookModalTitle'>Add Book</h2><br>" +
               "    <input type='hidden' id='bId'>" +
               "    <input type='text' id='bTitle' placeholder='Title' style='width:100%'><br>" +
               "    <input type='text' id='bAuthor' placeholder='Author' style='width:100%'><br>" +
               "    <input type='number' id='bQty' placeholder='Quantity' style='width:100%'><br><br>" +
               "    <button class='btn-primary' onclick='saveBook()'>Save</button> <button onclick='closeModal(\"bookModal\")'>Cancel</button>" +
               "  </div>" +
               "</div>" +
               "<div id='memberModal' style='display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); align-items:center; justify-content:center;'>" +
               "  <div class='card' style='width:400px'><h2 id='memberModalTitle'>Add Member</h2><br>" +
               "    <input type='hidden' id='mId'>" +
               "    <input type='text' id='mName' placeholder='Name' style='width:100%'><br>" +
               "    <input type='text' id='mPhone' placeholder='Phone' style='width:100%'><br><br>" +
               "    <button class='btn-primary' onclick='saveMember()'>Save</button> <button onclick='closeModal(\"memberModal\")'>Cancel</button>" +
               "  </div>" +
               "</div>" +
               "<script>" +
               "  let currentBooks = [];" +
               "  let currentMembers = [];" +
               "  function showSection(id) {" +
               "    document.querySelectorAll('.section').forEach(s => s.style.display = 'none');" +
               "    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));" +
               "    document.getElementById(id + '-section').style.display = 'block';" +
               "    document.getElementById('nav-' + id).classList.add('active');" +
               "    if(id === 'books') loadBooks();" +
               "    if(id === 'members') loadMembers();" +
               "  }" +
               "  function openModal(id, data = null) {" +
               "    document.getElementById(id).style.display = 'flex';" +
               "    if (id === 'bookModal') {" +
               "      if (data) {" +
               "        document.getElementById('bookModalTitle').innerText = 'Edit Book';" +
               "        document.getElementById('bId').value = data.id;" +
               "        document.getElementById('bTitle').value = data.title;" +
               "        document.getElementById('bAuthor').value = data.author;" +
               "        document.getElementById('bQty').value = data.quantity;" +
               "      } else {" +
               "        document.getElementById('bookModalTitle').innerText = 'Add Book';" +
               "        document.getElementById('bId').value = '';" +
               "        document.getElementById('bTitle').value = '';" +
               "        document.getElementById('bAuthor').value = '';" +
               "        document.getElementById('bQty').value = '';" +
               "      }" +
               "    } else if (id === 'memberModal') {" +
               "      if (data) {" +
               "        document.getElementById('memberModalTitle').innerText = 'Edit Member';" +
               "        document.getElementById('mId').value = data.id;" +
               "        document.getElementById('mName').value = data.name;" +
               "        document.getElementById('mPhone').value = data.phone;" +
               "      } else {" +
               "        document.getElementById('memberModalTitle').innerText = 'Add Member';" +
               "        document.getElementById('mId').value = '';" +
               "        document.getElementById('mName').value = '';" +
               "        document.getElementById('mPhone').value = '';" +
               "      }" +
               "    }" +
               "  }" +
               "  function closeModal(id) { document.getElementById(id).style.display = 'none'; }" +
               "  function loadBooks() {" +
               "    fetch('/api/books').then(r => r.json()).then(data => {" +
               "      currentBooks = data;" +
               "      const tbody = document.querySelector('#bookTable tbody');" +
               "      tbody.innerHTML = data.map(b => `<tr><td>${b.id}</td><td>${b.title}</td><td>${b.author}</td><td>${b.quantity}</td><td><button class='btn-edit' onclick='editBook(${b.id})'>Edit</button></td></tr>`).join('');" +
               "    });" +
               "  }" +
               "  function editBook(id) {" +
               "    const book = currentBooks.find(b => b.id === id);" +
               "    if (book) openModal('bookModal', book);" +
               "  }" +
               "  function saveBook() {" +
               "    const params = new URLSearchParams();" +
               "    const id = document.getElementById('bId').value;" +
               "    if (id) params.append('id', id);" +
               "    params.append('title', document.getElementById('bTitle').value);" +
               "    params.append('author', document.getElementById('bAuthor').value);" +
               "    params.append('quantity', document.getElementById('bQty').value);" +
               "    fetch('/api/books', {method:'POST', body: params}).then(() => { closeModal('bookModal'); loadBooks(); });" +
               "  }" +
               "  function loadMembers() {" +
               "    fetch('/api/members').then(r => r.json()).then(data => {" +
               "      currentMembers = data;" +
               "      const tbody = document.querySelector('#memberTable tbody');" +
               "      tbody.innerHTML = data.map(m => `<tr><td>${m.id}</td><td>${m.name}</td><td>${m.phone}</td><td><button class='btn-edit' onclick='editMember(${m.id})'>Edit</button></td></tr>`).join('');" +
               "    });" +
               "  }" +
               "  function editMember(id) {" +
               "    const member = currentMembers.find(m => m.id === id);" +
               "    if (member) openModal('memberModal', member);" +
               "  }" +
               "  function saveMember() {" +
               "    const params = new URLSearchParams();" +
               "    const id = document.getElementById('mId').value;" +
               "    if (id) params.append('id', id);" +
               "    params.append('name', document.getElementById('mName').value);" +
               "    params.append('phone', document.getElementById('mPhone').value);" +
               "    fetch('/api/members', {method:'POST', body: params}).then(() => { closeModal('memberModal'); loadMembers(); });" +
               "  }" +
               "  function issueBook() {" +
               "    const params = new URLSearchParams();" +
               "    params.append('bookId', document.getElementById('issueBid').value);" +
               "    params.append('memberId', document.getElementById('issueMid').value);" +
               "    fetch('/api/issue', {method:'POST', body: params}).then(r => r.text()).then(t => alert(t));" +
               "  }" +
               "  function returnBook() {" +
               "    const params = new URLSearchParams();" +
               "    params.append('bookId', document.getElementById('returnBid').value);" +
               "    params.append('memberId', document.getElementById('returnMid').value);" +
               "    fetch('/api/return', {method:'POST', body: params}).then(r => r.text()).then(t => alert(t));" +
               "  }" +
               "</script></body></html>";
    }
}
