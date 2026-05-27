import Foundation
import Shared

// ObservableObject-Wrapper um den Kotlin TodoStore.
// Swift kann Kotlin Coroutines/StateFlow nicht direkt nutzen –
// der TodoStore (iosMain) wandelt den Flow per Callback-Bridge in Swift-Land um.
@MainActor
class TodoObservable: ObservableObject {
    @Published var todos: [TodoItem] = []

    private var store: TodoStore?

    init() {
        store = TodoStore { [weak self] items in
            // items ist eine Kotlin List<TodoItem>, die als [TodoItem] ankommt.
            self?.todos = items as? [TodoItem] ?? []
        }
    }

    deinit {
        store?.dispose()
    }

    func addTodo(_ title: String) {
        store?.addTodo(title: title)
    }

    func toggleTodo(_ id: String) {
        store?.toggleTodo(id: id)
    }

    func deleteTodo(at offsets: IndexSet) {
        offsets.forEach { index in
            store?.deleteTodo(id: todos[index].id)
        }
    }
}
