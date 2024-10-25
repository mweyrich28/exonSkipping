import argparse
import sys
from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns


def read_and_process_directory(directory_path):
    """
    Read all appropriate files from the directory and return a dictionary of dataframes
    """
    dir_path = Path(directory_path)
    if not dir_path.exists():
        print(f"Error: Directory {directory_path} does not exist")
        sys.exit(1)

    # Dictionary to store dataframes by filename
    file_data = {}

    # Process all files with appropriate extensions
    for file_path in dir_path.glob("*.tsv"):  # Add more extensions if needed
        try:
            df = pd.read_csv(file_path, sep="\t")
            file_data[file_path.stem] = df
            print(f"Successfully processed {file_path.name}")
        except Exception as e:
            print(f"Error processing {file_path.name}: {e}")
            continue

    if not file_data:
        print("No valid files found in the directory")
        sys.exit(1)

    return file_data


def create_multi_cumulative_plot(file_data, column, title, output_path, use_log=True):
    """
    Create a cumulative distribution plot with optional log scale
    """
    plt.figure(figsize=(12, 7))

    # Create color palette for multiple files
    colors = sns.color_palette("husl", len(file_data))

    # Plot each file's distribution
    for (filename, df), color in zip(file_data.items(), colors):
        # Sort values and calculate cumulative distribution
        sorted_values = sorted(df[column])
        y = np.linspace(0, 1, len(sorted_values))

        # Create the plot
        plt.plot(sorted_values, y, "-", label=filename, color=color, linewidth=2)

    plt.grid(True, linestyle="--", alpha=0.7)

    # Apply log scale if requested
    if use_log:
        plt.xscale("log")
        plt.xlabel(f"{column.replace('_', ' ').title()} (log scale)")
    else:
        plt.xlabel(column.replace("_", " ").title())

    plt.ylabel("Cumulative Fraction")
    plt.title(f"Cumulative Distribution of {title}")

    # Add legend with smaller font and outside the plot
    plt.legend(bbox_to_anchor=(1.05, 1), loc="upper left", fontsize="small")

    # Adjust layout to prevent legend cutoff
    plt.tight_layout()
    plt.savefig(output_path, dpi=300, bbox_inches="tight")
    plt.close()


def generate_html_report(stats_dict, exons_plot_path, bases_plot_path):
    """
    Generate an HTML report with the analysis results
    """
    # Create the per-file statistics table HTML
    file_stats_rows = ""
    for filename, stats in stats_dict["per_file_stats"].items():
        file_stats_rows += f"""
        <tr>
            <td>{filename}</td>
            <td>{stats['total_events']}</td>
            <td>{stats['avg_skipped_exons']:.2f}</td>
            <td>{stats['max_skipped_exons']}</td>
            <td>{stats['avg_skipped_bases']:.2f}</td>
            <td>{stats['max_skipped_bases']}</td>
        </tr>
        """

    html_content = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <title>Exon Skipping Analysis Report</title>
        <style>
            body {{ font-family: Arial, sans-serif; margin: 40px; }}
            img {{ max-width: 1000px; }}
            .stats-table {{ border-collapse: collapse; margin: 20px 0; width: 100%; }}
            .stats-table td, .stats-table th {{ border: 1px solid #ddd; padding: 8px; text-align: left; }}
            .stats-table th {{ background-color: #f2f2f2; }}
            .plot-container {{ margin: 20px 0; }}
        </style>
    </head>
    <body>
        <h1>Exon Skipping Analysis Report</h1>
        
        <h2>Per-File Statistics</h2>
        <table class="stats-table">
            <tr>
                <th>File Name</th>
                <th>Total Events</th>
                <th>Avg Skipped Exons</th>
                <th>Max Skipped Exons</th>
                <th>Avg Skipped Bases</th>
                <th>Max Skipped Bases</th>
            </tr>
            {file_stats_rows}
        </table>

        <div class="plot-container">
            <h2>Distribution of Skipped Exons</h2>
            <img src="{exons_plot_path}" alt="Skipped Exons Distribution">
            <p>The plot above shows the cumulative distribution of the maximum number of skipped exons per ES-SE event
            for each input file. Each line represents a different file, allowing for comparison of exon skipping
            patterns between files.</p>
        </div>

        <div class="plot-container">
            <h2>Distribution of Skipped Bases</h2>
            <img src="{bases_plot_path}" alt="Skipped Bases Distribution">
            <p>This plot demonstrates the cumulative distribution of the maximum number of skipped bases per ES-SE event
            for each input file. The separate lines for each file enable comparison of the base skipping patterns
            across different files.</p>
        </div>

        <h2>Analysis Conclusions</h2>
        <ol>
            <li>The analysis covered {stats_dict['total_files']} different files, with varying numbers of ES-SE events per file.</li>
            <li>Across all files, the maximum number of skipped exons ranges from {stats_dict['overall_min_skipped_exons']} 
                to {stats_dict['overall_max_skipped_exons']}.</li>
            <li>The maximum number of skipped bases shows greater variation, ranging from {stats_dict['overall_min_skipped_bases']} 
                to {stats_dict['overall_max_skipped_bases']} bases.</li>
            <li>The distribution patterns vary between files, suggesting potential file-specific characteristics in exon skipping events.</li>
        </ol>
    </body>
    </html>
    """
    return html_content


def main():
    # Set up argument parser
    parser = argparse.ArgumentParser(
        description="Analyze exon skipping events from GTF files"
    )
    parser.add_argument(
        "--input_dir", help="Directory containing the GTF files to analyze"
    )
    parser.add_argument(
        "--output-dir",
        default="output_plot",
        help="Directory for output files (default: output)",
    )
    args = parser.parse_args()

    # Create output directory if it doesn't exist
    output_dir = Path(args.output_dir)
    output_dir.mkdir(exist_ok=True)

    # Read and process data from all files
    file_data = read_and_process_directory(args.input_dir)

    # Create plots
    create_multi_cumulative_plot(
        file_data,
        "max_skipped_exon",
        "Maximum Skipped Exons per ES-SE",
        output_dir / "skipped_exons.jpg",
        use_log=True,
    )

    create_multi_cumulative_plot(
        file_data,
        "max_skipped_bases",
        "Maximum Skipped Bases per ES-SE",
        output_dir / "skipped_bases.jpg",
        use_log=True,
    )

    # Calculate statistics for each file
    stats = {
        "per_file_stats": {},
        "total_files": len(file_data),
        "overall_max_skipped_exons": max(
            df["max_skipped_exon"].max() for df in file_data.values()
        ),
        "overall_min_skipped_exons": min(
            df["max_skipped_exon"].min() for df in file_data.values()
        ),
        "overall_max_skipped_bases": max(
            df["max_skipped_bases"].max() for df in file_data.values()
        ),
        "overall_min_skipped_bases": min(
            df["max_skipped_bases"].min() for df in file_data.values()
        ),
    }

    for filename, df in file_data.items():
        stats["per_file_stats"][filename] = {
            "total_events": len(df),
            "avg_skipped_exons": df["max_skipped_exon"].mean(),
            "max_skipped_exons": df["max_skipped_exon"].max(),
            "avg_skipped_bases": df["max_skipped_bases"].mean(),
            "max_skipped_bases": df["max_skipped_bases"].max(),
        }

    # Generate HTML report
    html_content = generate_html_report(stats, "skipped_exons.jpg", "skipped_bases.jpg")

    # Save HTML report
    with open(output_dir / "exon_skipping.html", "w") as f:
        f.write(html_content)

    # Generate PDF (requires wkhtmltopdf)
    try:
        import pdfkit

        pdfkit.from_file(
            str(output_dir / "exon_skipping.html"),
            str(output_dir / "exon_skipping.pdf"),
        )
    except Exception as e:
        print(f"Could not generate PDF: {e}")
        print("Please install wkhtmltopdf or convert HTML to PDF manually")


if __name__ == "__main__":
    main()
