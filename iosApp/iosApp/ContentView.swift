import SwiftUI
import Shared

struct ContentView: View {
    @StateObject private var viewModel = TodoObservable()
    @State private var newTodoTitle = ""

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                HStack {
                    TextField("Neue Aufgabe.", text: $newTodoTitle)
                        .textFieldStyle(.roundedBorder)
                        .submitLabel(.done)
                        .onSubmit { addTodo() }

                    Button(action: addTodo) {
                        Image(systemName: "plus.circle.fill")
                            .font(.title2)
                            .foregroundStyle(.blue)
                    }
                    .disabled(newTodoTitle.trimmingCharacters(in: .whitespaces).isEmpty)
                }
                .padding()

                if viewModel.todos.isEmpty {
                    Spacer()
                    Text("Keine Aufgaben vorhanden.")
                        .foregroundStyle(.secondary)
                    Spacer()
                } else {
                    List {
                        ForEach(viewModel.todos, id: \.id) { todo in
                            TodoRow(
                                todo: todo,
                                onToggle: { viewModel.toggleTodo(todo.id) }
                            )
                        }
                        .onDelete { offsets in
                            viewModel.deleteTodo(at: offsets)
                        }
                    }
                    .listStyle(.plain)
                }
            }
            .navigationTitle("ToDo App")
        }
    }

    private func addTodo() {
        let trimmed = newTodoTitle.trimmingCharacters(in: .whitespaces)
        guard !trimmed.isEmpty else { return }
        viewModel.addTodo(trimmed)
        newTodoTitle = ""
    }
}

struct TodoRow: View {
    let todo: TodoItem
    let onToggle: () -> Void

    var body: some View {
        HStack {
            Button(action: onToggle) {
                Image(systemName: todo.isCompleted ? "checkmark.circle.fill" : "circle")
                    .foregroundStyle(todo.isCompleted ? .green : .secondary)
                    .font(.title3)
            }
            .buttonStyle(.plain)

            Text(todo.title)
                .strikethrough(todo.isCompleted)
                .foregroundStyle(todo.isCompleted ? .secondary : .primary)

            Spacer()
        }
        .contentShape(Rectangle())
    }
}
